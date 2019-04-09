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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SendMailRequest {

    private String subject;
    private String body;
    private String htmlBody;
    private String senderCode;
    private String contentType;
    private String[] recipientsTo;
    private String[] recipientsCc;
    private String[] recipientsBcc;
    private List<Attachment> attachments;
    private String templateName;
    private String templateLang;
    private Map<String, String> templateParams;

    public String getSubject() {
        return subject;
    }

    public SendMailRequest setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getBody() {
        return body;
    }

    public SendMailRequest setBody(String body) {
        this.body = body;
        return this;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public SendMailRequest setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
        return this;
    }

    public String getSenderCode() {
        return senderCode;
    }

    public SendMailRequest setSenderCode(String senderCode) {
        this.senderCode = senderCode;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public SendMailRequest setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String[] getRecipientsTo() {
        return recipientsTo;
    }

    public SendMailRequest setRecipientsTo(String[] recipientsTo) {
        this.recipientsTo = recipientsTo;
        return this;
    }

    public String[] getRecipientsCc() {
        return recipientsCc;
    }

    public SendMailRequest setRecipientsCc(String[] recipientsCc) {
        this.recipientsCc = recipientsCc;
        return this;
    }

    public String[] getRecipientsBcc() {
        return recipientsBcc;
    }

    public SendMailRequest setRecipientsBcc(String[] recipientsBcc) {
        this.recipientsBcc = recipientsBcc;
        return this;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public SendMailRequest setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public SendMailRequest addAttachment(Attachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
        return this;
    }

    public String getTemplateName() {
        return templateName;
    }

    public SendMailRequest setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public Map<String, String> getTemplateParams() {
        return templateParams;
    }

    public SendMailRequest setTemplateParams(Map<String, String> templateParams) {
        this.templateParams = templateParams;
        return this;
    }

    public String getTemplateLang() {
        return templateLang;
    }

    public SendMailRequest setTemplateLang(String templateLang) {
        this.templateLang = templateLang;
        return this;
    }
}
