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

import org.entando.plugin.mail.EntandoMailPluginApp;
import org.entando.plugin.mail.service.MailService;
import org.entando.plugin.mail.service.SendMailRequest;
import org.entando.plugin.mail.web.rest.TestUtil;
import org.entando.plugin.mail.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import static org.entando.plugin.mail.web.rest.TestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EntandoMailPluginApp.class)
public class SendMailResourceTest {

    @Mock
    private MailService mailService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc sendMailMockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        final SendMailResource emailTemplateResource = new SendMailResource(mailService);
        sendMailMockMvc = MockMvcBuilders.standaloneSetup(emailTemplateResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    @Test
    public void testSendMail() throws Exception {

        SendMailRequest mailRequest = new SendMailRequest()
                .setSubject("Simple mail")
                .setBody("body")
                .setRecipientsTo(new String[]{"test@entando.org"})
                .setSenderCode("sender1");

        sendMailMockMvc.perform(post("/api/mail")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(mailRequest)))
                .andExpect(status().isCreated());

        verify(mailService, times(1)).sendMail(any());
    }
}
