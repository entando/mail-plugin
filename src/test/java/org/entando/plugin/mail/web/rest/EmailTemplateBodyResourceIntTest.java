package org.entando.plugin.mail.web.rest;

import org.entando.plugin.mail.MailPluginApp;

import org.entando.plugin.mail.domain.EmailTemplateBody;
import org.entando.plugin.mail.repository.EmailTemplateBodyRepository;
import org.entando.plugin.mail.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static org.entando.plugin.mail.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EmailTemplateBodyResource REST controller.
 *
 * @see EmailTemplateBodyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailPluginApp.class)
public class EmailTemplateBodyResourceIntTest {

    private static final String DEFAULT_LANG = "AAAAAAAAAA";
    private static final String UPDATED_LANG = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    @Autowired
    private EmailTemplateBodyRepository emailTemplateBodyRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEmailTemplateBodyMockMvc;

    private EmailTemplateBody emailTemplateBody;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EmailTemplateBodyResource emailTemplateBodyResource = new EmailTemplateBodyResource(emailTemplateBodyRepository);
        this.restEmailTemplateBodyMockMvc = MockMvcBuilders.standaloneSetup(emailTemplateBodyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmailTemplateBody createEntity(EntityManager em) {
        EmailTemplateBody emailTemplateBody = new EmailTemplateBody()
            .lang(DEFAULT_LANG)
            .body(DEFAULT_BODY);
        return emailTemplateBody;
    }

    @Before
    public void initTest() {
        emailTemplateBody = createEntity(em);
    }

    @Test
    @Transactional
    public void createEmailTemplateBody() throws Exception {
        int databaseSizeBeforeCreate = emailTemplateBodyRepository.findAll().size();

        // Create the EmailTemplateBody
        restEmailTemplateBodyMockMvc.perform(post("/api/email-template-bodies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailTemplateBody)))
            .andExpect(status().isCreated());

        // Validate the EmailTemplateBody in the database
        List<EmailTemplateBody> emailTemplateBodyList = emailTemplateBodyRepository.findAll();
        assertThat(emailTemplateBodyList).hasSize(databaseSizeBeforeCreate + 1);
        EmailTemplateBody testEmailTemplateBody = emailTemplateBodyList.get(emailTemplateBodyList.size() - 1);
        assertThat(testEmailTemplateBody.getLang()).isEqualTo(DEFAULT_LANG);
        assertThat(testEmailTemplateBody.getBody()).isEqualTo(DEFAULT_BODY);
    }

    @Test
    @Transactional
    public void createEmailTemplateBodyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = emailTemplateBodyRepository.findAll().size();

        // Create the EmailTemplateBody with an existing ID
        emailTemplateBody.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmailTemplateBodyMockMvc.perform(post("/api/email-template-bodies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailTemplateBody)))
            .andExpect(status().isBadRequest());

        // Validate the EmailTemplateBody in the database
        List<EmailTemplateBody> emailTemplateBodyList = emailTemplateBodyRepository.findAll();
        assertThat(emailTemplateBodyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkLangIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailTemplateBodyRepository.findAll().size();
        // set the field null
        emailTemplateBody.setLang(null);

        // Create the EmailTemplateBody, which fails.

        restEmailTemplateBodyMockMvc.perform(post("/api/email-template-bodies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailTemplateBody)))
            .andExpect(status().isBadRequest());

        List<EmailTemplateBody> emailTemplateBodyList = emailTemplateBodyRepository.findAll();
        assertThat(emailTemplateBodyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBodyIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailTemplateBodyRepository.findAll().size();
        // set the field null
        emailTemplateBody.setBody(null);

        // Create the EmailTemplateBody, which fails.

        restEmailTemplateBodyMockMvc.perform(post("/api/email-template-bodies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailTemplateBody)))
            .andExpect(status().isBadRequest());

        List<EmailTemplateBody> emailTemplateBodyList = emailTemplateBodyRepository.findAll();
        assertThat(emailTemplateBodyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEmailTemplateBodies() throws Exception {
        // Initialize the database
        emailTemplateBodyRepository.saveAndFlush(emailTemplateBody);

        // Get all the emailTemplateBodyList
        restEmailTemplateBodyMockMvc.perform(get("/api/email-template-bodies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emailTemplateBody.getId().intValue())))
            .andExpect(jsonPath("$.[*].lang").value(hasItem(DEFAULT_LANG.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())));
    }
    
    @Test
    @Transactional
    public void getEmailTemplateBody() throws Exception {
        // Initialize the database
        emailTemplateBodyRepository.saveAndFlush(emailTemplateBody);

        // Get the emailTemplateBody
        restEmailTemplateBodyMockMvc.perform(get("/api/email-template-bodies/{id}", emailTemplateBody.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(emailTemplateBody.getId().intValue()))
            .andExpect(jsonPath("$.lang").value(DEFAULT_LANG.toString()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEmailTemplateBody() throws Exception {
        // Get the emailTemplateBody
        restEmailTemplateBodyMockMvc.perform(get("/api/email-template-bodies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmailTemplateBody() throws Exception {
        // Initialize the database
        emailTemplateBodyRepository.saveAndFlush(emailTemplateBody);

        int databaseSizeBeforeUpdate = emailTemplateBodyRepository.findAll().size();

        // Update the emailTemplateBody
        EmailTemplateBody updatedEmailTemplateBody = emailTemplateBodyRepository.findById(emailTemplateBody.getId()).get();
        // Disconnect from session so that the updates on updatedEmailTemplateBody are not directly saved in db
        em.detach(updatedEmailTemplateBody);
        updatedEmailTemplateBody
            .lang(UPDATED_LANG)
            .body(UPDATED_BODY);

        restEmailTemplateBodyMockMvc.perform(put("/api/email-template-bodies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedEmailTemplateBody)))
            .andExpect(status().isOk());

        // Validate the EmailTemplateBody in the database
        List<EmailTemplateBody> emailTemplateBodyList = emailTemplateBodyRepository.findAll();
        assertThat(emailTemplateBodyList).hasSize(databaseSizeBeforeUpdate);
        EmailTemplateBody testEmailTemplateBody = emailTemplateBodyList.get(emailTemplateBodyList.size() - 1);
        assertThat(testEmailTemplateBody.getLang()).isEqualTo(UPDATED_LANG);
        assertThat(testEmailTemplateBody.getBody()).isEqualTo(UPDATED_BODY);
    }

    @Test
    @Transactional
    public void updateNonExistingEmailTemplateBody() throws Exception {
        int databaseSizeBeforeUpdate = emailTemplateBodyRepository.findAll().size();

        // Create the EmailTemplateBody

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailTemplateBodyMockMvc.perform(put("/api/email-template-bodies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailTemplateBody)))
            .andExpect(status().isBadRequest());

        // Validate the EmailTemplateBody in the database
        List<EmailTemplateBody> emailTemplateBodyList = emailTemplateBodyRepository.findAll();
        assertThat(emailTemplateBodyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEmailTemplateBody() throws Exception {
        // Initialize the database
        emailTemplateBodyRepository.saveAndFlush(emailTemplateBody);

        int databaseSizeBeforeDelete = emailTemplateBodyRepository.findAll().size();

        // Delete the emailTemplateBody
        restEmailTemplateBodyMockMvc.perform(delete("/api/email-template-bodies/{id}", emailTemplateBody.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<EmailTemplateBody> emailTemplateBodyList = emailTemplateBodyRepository.findAll();
        assertThat(emailTemplateBodyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmailTemplateBody.class);
        EmailTemplateBody emailTemplateBody1 = new EmailTemplateBody();
        emailTemplateBody1.setId(1L);
        EmailTemplateBody emailTemplateBody2 = new EmailTemplateBody();
        emailTemplateBody2.setId(emailTemplateBody1.getId());
        assertThat(emailTemplateBody1).isEqualTo(emailTemplateBody2);
        emailTemplateBody2.setId(2L);
        assertThat(emailTemplateBody1).isNotEqualTo(emailTemplateBody2);
        emailTemplateBody1.setId(null);
        assertThat(emailTemplateBody1).isNotEqualTo(emailTemplateBody2);
    }
}
