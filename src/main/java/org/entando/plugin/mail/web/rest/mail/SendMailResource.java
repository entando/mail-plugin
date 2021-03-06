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
package org.entando.plugin.mail.web.rest.mail;

import org.apache.commons.lang3.StringUtils;
import org.entando.plugin.mail.service.MailService;
import org.entando.plugin.mail.service.SendMailRequest;
import org.entando.plugin.mail.web.rest.errors.BadRequestAlertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SendMailResource {

    private final MailService mailService;

    @Autowired
    public SendMailResource(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/mail")
    public ResponseEntity<?> sendMail(@RequestBody SendMailRequest sendMailRequest) {

        if (!StringUtils.isEmpty(sendMailRequest.getTemplateName())
                && StringUtils.isEmpty(sendMailRequest.getTemplateLang())) {
            throw new BadRequestAlertException("When using a template it is necessary to specify also a language", "SendMailRequest", "langmissing");
        }

        mailService.sendMail(sendMailRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
