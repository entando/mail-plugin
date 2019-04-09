/*
 * Copyright 2019-Present Entando Inc. (http://www.entando.com) All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.plugin.mail.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.lang.StringUtils;
import org.entando.plugin.mail.domain.EmailSender;
import org.entando.plugin.mail.domain.EmailTemplate;
import org.entando.plugin.mail.domain.SmtpServerConfig;
import org.entando.plugin.mail.repository.EmailSenderRepository;
import org.entando.plugin.mail.repository.EmailTemplateRepository;
import org.entando.plugin.mail.repository.SmtpServerConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    /**
     * Default Timeout in milliseconds
     */
    private static final int DEFAULT_SMTP_TIMEOUT = 5000;
    private static final String CONTENTTYPE_TEXT_PLAIN = "text/plain";
    private static final String CONTENTTYPE_TEXT_HTML = "text/html";
    private static final int DEFAULT_SMTP_PORT = 25;

    private final SmtpServerConfig smtpServerConfig;
    private final EmailSenderRepository senderRepository;
    private final EmailTemplateRepository templateRepository;

    @Autowired
    public MailService(SmtpServerConfigRepository smtpRepository, EmailSenderRepository senderRepository,
            EmailTemplateRepository templateRepository) {
        List<SmtpServerConfig> configs = smtpRepository.findAll();
        if (configs.isEmpty()) {
            throw new IllegalStateException("SMTP Server not configured");
        }
        smtpServerConfig = configs.get(0);
        this.senderRepository = senderRepository;
        this.templateRepository = templateRepository;
    }

    public boolean smtpServerTest() {
        try {
            Session session = prepareSession();
            try (Transport bus = session.getTransport("smtp")) {
                if (StringUtils.isEmpty(smtpServerConfig.getUsername())) {
                    bus.connect();
                } else {
                    bus.connect(smtpServerConfig.getHost(), smtpServerConfig.getPort(), smtpServerConfig.getUsername(), smtpServerConfig.getPassword());
                }
            }
            return true;
        } catch (MessagingException ex) {
            return false;
        }
    }

    public void sendMail(SendMailRequest mailRequest) {
        if (!smtpServerConfig.isActive()) {
            logger.info("Sender function disabled : mail Subject {}", mailRequest.getSubject());
            return;
        }

        Session session = prepareSession();
        try (Transport bus = prepareTransport(session)) {

            MimeMessage msg = prepareVoidMimeMessage(session, mailRequest);

            String body = mailRequest.getBody();
            if (!StringUtils.isEmpty(mailRequest.getTemplateName())) {
                body = replaceTemplateParams(mailRequest);
            }

            boolean hasAttachments = mailRequest.getAttachments() != null && mailRequest.getAttachments().size() > 0;
            boolean hasHtmlAlternativeBody = !StringUtils.isEmpty(mailRequest.getHtmlBody());

            String bodyContentType = CONTENTTYPE_TEXT_PLAIN;
            if (!StringUtils.isEmpty(mailRequest.getContentType())) {
                bodyContentType = mailRequest.getContentType();
            }

            if (hasAttachments || hasHtmlAlternativeBody) {

                MimeMultipart multiPart;

                if (hasHtmlAlternativeBody) {
                    String multipartMimeType = hasAttachments ? "mixed" : "alternative";
                    multiPart = new MimeMultipart(multipartMimeType);
                    addBodyPart(body, bodyContentType, multiPart);
                    addBodyPart(mailRequest.getHtmlBody(), CONTENTTYPE_TEXT_HTML, multiPart);
                } else {
                    multiPart = new MimeMultipart();
                    addBodyPart(body, bodyContentType, multiPart);
                }

                if (hasAttachments) {
                    addAttachments(mailRequest.getAttachments(), multiPart);
                }

                msg.setContent(multiPart);

            } else {
                // Simple message
                msg.setContent(body, bodyContentType + "; charset=utf-8");
            }

            msg.saveChanges();
            Transport.send(msg);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Prepare a Transport object ready for use.
     *
     * @param session A session object.
     * @param config The configuration
     * @return The Transport object ready for use.
     * @throws Exception In case of errors opening the Transport object.
     */
    private Transport prepareTransport(Session session) {
        try {
            Transport bus = session.getTransport("smtp");
            if (smtpServerConfig.hasAnonimousAuth()) {
                bus.connect();
            }
            return bus;
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Prepare a Session object ready for use.
     *
     * @param config The configuration
     * @return The Session object ready for use.
     */
    private Session prepareSession() {
        Properties props = System.getProperties();
        Session session;
        // Timeout
        int timeout = DEFAULT_SMTP_TIMEOUT;
        Integer timeoutParam = smtpServerConfig.getTimeout();
        if (null != timeoutParam && timeoutParam != 0) {
            timeout = timeoutParam;
        }
        props.put("mail.smtp.connectiontimeout", timeout);
        props.put("mail.smtp.timeout", timeout);
        // Debug
        if (smtpServerConfig.isDebug()) {
            props.put("mail.debug", "true");
        }
        // port
        Integer port = smtpServerConfig.getPort();
        if (null != port && port > 0) {
            props.put("mail.smtp.port", port.toString());
        } else {
            props.put("mail.smtp.port", String.valueOf(DEFAULT_SMTP_PORT));
        }
        // host
        props.put("mail.smtp.host", smtpServerConfig.getHost());
        // auth
        if (!smtpServerConfig.hasAnonimousAuth()) {
            props.put("mail.smtp.auth", "true");
            switch (smtpServerConfig.getSecurity()) {
                case SSL:
                    props.put("mail.smtp.socketFactory.port", port);
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.transport.protocol", "smtps");
                    break;
                case TLS:
                    props.put("mail.smtp.starttls.enable", "true");
                    break;
                case NONE:
                // do nothing just use previous properties WITH the authenticator
            }
            Authenticator auth = new SMTPAuthenticator(smtpServerConfig);
            session = Session.getInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props);
        }
        return session;
    }

    /**
     * Prepare a MimeMessage complete of sender, recipient addresses, subject
     * and current date but lacking in the message text content.
     *
     * @param session The session object.
     * @param subject The e-mail subject.
     * @param recipientsTo The e-mail main destination addresses.
     * @param recipientsCc The e-mail 'carbon-copy' destination addresses.
     * @param recipientsBcc The e-mail 'blind carbon-copy' destination
     * addresses.
     * @param senderCode The sender code, as configured in the service
     * configuration.
     * @return A mime message without message text content.
     * @throws MessagingException In case of errors preparing the mail message.
     */
    private MimeMessage prepareVoidMimeMessage(Session session, SendMailRequest mailRequest) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);

        EmailSender sender = senderRepository.findByName(mailRequest.getSenderCode())
                .orElseThrow(() -> new IllegalArgumentException("Sender " + mailRequest.getSenderCode() + " not found"));

        msg.setFrom(new InternetAddress(sender.getEmail()));
        msg.setSubject(mailRequest.getSubject());
        msg.setSentDate(new Date());

        this.addRecipients(msg, Message.RecipientType.TO, mailRequest.getRecipientsTo());
        this.addRecipients(msg, Message.RecipientType.CC, mailRequest.getRecipientsCc());
        this.addRecipients(msg, Message.RecipientType.BCC, mailRequest.getRecipientsBcc());

        msg.saveChanges();

        return msg;
    }

    /**
     * Add a BodyPart to the Multipart container.
     *
     * @param text The text content.
     * @param contentType The text contentType.
     * @param multiPart The Multipart container.
     * @throws MessagingException In case of errors adding the body part.
     */
    private void addBodyPart(String text, String contentType, Multipart multiPart) throws MessagingException {
        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setContent(text, contentType + "; charset=utf-8");
        multiPart.addBodyPart(textBodyPart);
    }

    /**
     * Add the attachments to the Multipart container.
     *
     * @param attachments The attachments mapped as fileName/filePath.
     * @param multiPart The Multipart container.
     * @throws MessagingException In case of errors adding the attachments.
     */
    private void addAttachments(List<Attachment> attachments, Multipart multiPart) throws MessagingException {

        for (Attachment attachment : attachments) {
            MimeBodyPart fileBodyPart = new MimeBodyPart();
            ByteArrayDataSource bds = new ByteArrayDataSource(attachment.getContent(), attachment.getContentType());
            fileBodyPart.setDataHandler(new DataHandler(bds));
            fileBodyPart.setFileName(attachment.getName());
            multiPart.addBodyPart(fileBodyPart);
        }
    }

    /**
     * Add recipient addresses to the e-mail.
     *
     * @param msg The mime message to which add the addresses.
     * @param recType The specific recipient type to which add the addresses.
     * @param recipients The recipient addresses.
     */
    private void addRecipients(MimeMessage msg, Message.RecipientType recType, String[] recipients) {
        if (null != recipients) {
            try {
                Address[] addresses = new Address[recipients.length];
                for (int i = 0; i < recipients.length; i++) {
                    Address address = new InternetAddress(recipients[i]);
                    addresses[i] = address;
                }
                msg.setRecipients(recType, addresses);
            } catch (MessagingException e) {
                throw new RuntimeException("Error adding recipients", e);
            }
        }
    }

    private String replaceTemplateParams(SendMailRequest mailRequest) {

        String templateName = mailRequest.getTemplateName();
        String templateLang = mailRequest.getTemplateLang();
        Map<String, String> params = mailRequest.getTemplateParams();

        EmailTemplate template = templateRepository.findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template " + templateName + " not found"));

        String body = template.getBodies().stream()
                .filter(t -> t.getLang().equals(templateLang))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Template " + templateName + " has no language " + templateLang))
                .getBody();

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String field = "{" + entry.getKey() + "}";
                int start = body.indexOf(field);
                if (start > 0) {
                    int end = start + field.length();
                    StringBuilder sb = new StringBuilder();
                    sb.append(body.substring(0, start));
                    sb.append(entry.getValue());
                    sb.append(body.substring(end));
                    body = sb.toString();
                }
            }
        }

        return body;
    }
}
