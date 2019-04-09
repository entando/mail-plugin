/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.entando.plugin.mail.service;

import com.google.common.collect.ImmutableSet;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.entando.plugin.mail.domain.EmailTemplate;
import org.entando.plugin.mail.repository.EmailSenderRepository;
import org.entando.plugin.mail.repository.EmailTemplateRepository;
import org.entando.plugin.mail.repository.SmtpServerConfigRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.subethamail.wiser.Wiser;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import org.entando.plugin.mail.domain.EmailTemplateBody;
import static org.mockito.Mockito.when;
import static org.entando.plugin.mail.service.MailConfigTestUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class TestMailService {

    private final String MAIL_TEXT = "Test mail";
    private final String MAIL_HTML_TEXT = "<a href=\"https://www.entando.com/\" >Test mail</a>";
    private final String TEMPLATE_NAME = "template1";

    private Wiser wiser;

    @Mock
    private SmtpServerConfigRepository smtpConfigRepository;

    @Mock
    private EmailSenderRepository senderRepository;

    @Mock
    private EmailTemplateRepository templateRepository;

    private MailService mailManager;

    @Before
    public void setUp() {

        when(smtpConfigRepository.findAll()).thenReturn(
                Collections.singletonList(smtpServerConfig()));

        when(senderRepository.findByName(SENDER_1)).thenReturn(Optional.of(sender1()));

        when(templateRepository.findByName(TEMPLATE_NAME)).thenReturn(Optional.of(emailTemplate()));

        wiser = new Wiser();
        wiser.setPort(SMTP_PORT);
        wiser.setHostname(SMTP_HOST);
        wiser.start();

        mailManager = new MailService(smtpConfigRepository, senderRepository, templateRepository);
    }

    @After
    public void tearDown() {
        wiser.stop();
    }

    @Test
    public void testSendMail() throws Exception {

        SendMailRequest mailRequest = new SendMailRequest()
                .setSubject("Simple mail")
                .setBody(MAIL_TEXT)
                .setRecipientsTo(MAIL_ADDRESSES)
                .setSenderCode(SENDER_1);

        mailManager.sendMail(mailRequest);

        assertThat(wiser.getMessages()).hasSize(1);
    }

    @Test
    public void testSendMailWithHtmlBody() throws Exception {

        SendMailRequest mailRequest = new SendMailRequest()
                .setSubject("HTML mail")
                .setBody(MAIL_TEXT)
                .setHtmlBody(MAIL_HTML_TEXT)
                .setRecipientsTo(MAIL_ADDRESSES)
                .setSenderCode(SENDER_1);

        mailManager.sendMail(mailRequest);

        assertThat(wiser.getMessages()).hasSize(1);
    }

    @Test
    public void testSendMailWithAttachments() throws Exception {

        SendMailRequest mailRequest = new SendMailRequest()
                .setSubject("Mail with attachments")
                .setBody(MAIL_TEXT)
                .setRecipientsTo(MAIL_ADDRESSES)
                .setSenderCode(SENDER_1)
                .addAttachment(attachment());

        mailManager.sendMail(mailRequest);

        assertThat(wiser.getMessages()).hasSize(1);
    }

    @Test
    public void testSendMailWithHtmlBodyAndAttachments() throws Exception {

        SendMailRequest mailRequest = new SendMailRequest()
                .setSubject("Mail with attachments and HTML body")
                .setBody(MAIL_TEXT)
                .setHtmlBody(MAIL_HTML_TEXT)
                .setRecipientsTo(MAIL_ADDRESSES)
                .setSenderCode(SENDER_1)
                .addAttachment(attachment());

        mailManager.sendMail(mailRequest);

        assertThat(wiser.getMessages()).hasSize(1);
    }

    @Test
    public void testSendMailWithTemplate() throws Exception {

        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("placeholder", "value");

        SendMailRequest mailRequest = new SendMailRequest()
                .setRecipientsTo(MAIL_ADDRESSES)
                .setSenderCode(SENDER_1)
                .setTemplateName(TEMPLATE_NAME)
                .setTemplateLang("en")
                .setTemplateParams(templateParams);

        mailManager.sendMail(mailRequest);

        assertThat(wiser.getMessages()).hasSize(1);

        String body = new String(wiser.getMessages().get(0).getData(), StandardCharsets.UTF_8);
        assertThat(body).contains("[my template value]");
    }

    @Test
    public void testSmtpServerTest() {
        assertThat(mailManager.smtpServerTest()).isTrue();
    }

    private Attachment attachment() {
        Attachment attachment = new Attachment();
        attachment.setName("test-attachment")
                .setContentType("text/plain")
                .setContent("content".getBytes());
        return attachment;
    }

    private EmailTemplate emailTemplate() {

        EmailTemplate emailTemplate = new EmailTemplate();

        emailTemplate.setName(TEMPLATE_NAME);
        emailTemplate.setSubject("test template");
        emailTemplate.setBodies(ImmutableSet.of(templateBody()));

        return emailTemplate;
    }

    private EmailTemplateBody templateBody() {
        EmailTemplateBody templateBody = new EmailTemplateBody();

        templateBody.setId(1L);
        templateBody.setLang("en");
        templateBody.setBody("[my template {placeholder}]");

        return templateBody;
    }
}
