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
import org.entando.plugin.mail.domain.EmailSender;
import org.entando.plugin.mail.domain.SmtpServerConfig;
import org.entando.plugin.mail.domain.enumeration.SmtpSecurity;

public class MailConfigTestUtils {

    public static final String SMTP_HOST = "localhost";
    public static final int SMTP_PORT = 2525;
    public static final String SMTP_USERNAME = "smtp-user";
    public static final String SMTP_PASSWORD = "smtp-password";
    public static final String SENDER_1 = "sender1";
    public static final String SENDER_2 = "sender2";
    public static final String[] MAIL_ADDRESSES = {"testuser@localhost"};

    private MailConfigTestUtils() {
    }

    public static SmtpServerConfig smtpServerConfig() {
        SmtpServerConfig config = new SmtpServerConfig();

        config.setHost(SMTP_HOST);
        config.setPort(SMTP_PORT);
        config.setTimeout(500);
        config.setUsername(SMTP_USERNAME);
        config.setPassword(SMTP_PASSWORD);
        config.setSecurity(SmtpSecurity.TLS);
        config.setActive(true);

        return config;
    }

    public static EmailSender sender1() {
        EmailSender sender1 = new EmailSender();
        sender1.setId(1L);
        sender1.setName(SENDER_1);
        sender1.setEmail("sender1@entando.org");
        return sender1;
    }

    public static EmailSender sender2() {
        EmailSender sender2 = new EmailSender();
        sender2.setId(2L);
        sender2.setName(SENDER_2);
        sender2.setEmail("sender2@entando.org");
        return sender2;
    }

    public static List<EmailSender> emailSenders() {
        List<EmailSender> senders = new ArrayList<>();
        senders.add(sender1());
        senders.add(sender2());
        return senders;
    }
}
