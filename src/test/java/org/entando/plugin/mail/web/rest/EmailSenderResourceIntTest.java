package org.entando.plugin.mail.web.rest;

import org.entando.plugin.mail.EntandoMailPluginApp;

import org.entando.plugin.mail.domain.EmailSender;
import org.entando.plugin.mail.repository.EmailSenderRepository;
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
 * Test class for the EmailSenderResource REST controller.
 *
 * @see EmailSenderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EntandoMailPluginApp.class)
public class EmailSenderResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    @Autowired
    private EmailSenderRepository emailSenderRepository;

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

    private MockMvc restEmailSenderMockMvc;

    private EmailSender emailSender;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EmailSenderResource emailSenderResource = new EmailSenderResource(emailSenderRepository);
        this.restEmailSenderMockMvc = MockMvcBuilders.standaloneSetup(emailSenderResource)
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
    public static EmailSender createEntity(EntityManager em) {
        EmailSender emailSender = new EmailSender()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL);
        return emailSender;
    }

    @Before
    public void initTest() {
        emailSender = createEntity(em);
    }

    @Test
    @Transactional
    public void createEmailSender() throws Exception {
        int databaseSizeBeforeCreate = emailSenderRepository.findAll().size();

        // Create the EmailSender
        restEmailSenderMockMvc.perform(post("/api/email-senders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailSender)))
            .andExpect(status().isCreated());

        // Validate the EmailSender in the database
        List<EmailSender> emailSenderList = emailSenderRepository.findAll();
        assertThat(emailSenderList).hasSize(databaseSizeBeforeCreate + 1);
        EmailSender testEmailSender = emailSenderList.get(emailSenderList.size() - 1);
        assertThat(testEmailSender.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEmailSender.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void createEmailSenderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = emailSenderRepository.findAll().size();

        // Create the EmailSender with an existing ID
        emailSender.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmailSenderMockMvc.perform(post("/api/email-senders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailSender)))
            .andExpect(status().isBadRequest());

        // Validate the EmailSender in the database
        List<EmailSender> emailSenderList = emailSenderRepository.findAll();
        assertThat(emailSenderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createEmailSenderWithExistingName() throws Exception {

        EmailSender sender1 = new EmailSender();
        sender1.setName("sender1");
        sender1.setEmail("sender1@entando.org");

        emailSenderRepository.save(sender1);

        int databaseSizeBeforeCreate = emailSenderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmailSenderMockMvc.perform(post("/api/email-senders")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sender1)))
                .andExpect(status().isBadRequest());

        // Validate the EmailSender in the database
        List<EmailSender> emailSenderList = emailSenderRepository.findAll();
        assertThat(emailSenderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailSenderRepository.findAll().size();
        // set the field null
        emailSender.setName(null);

        // Create the EmailSender, which fails.

        restEmailSenderMockMvc.perform(post("/api/email-senders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailSender)))
            .andExpect(status().isBadRequest());

        List<EmailSender> emailSenderList = emailSenderRepository.findAll();
        assertThat(emailSenderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailSenderRepository.findAll().size();
        // set the field null
        emailSender.setEmail(null);

        // Create the EmailSender, which fails.

        restEmailSenderMockMvc.perform(post("/api/email-senders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailSender)))
            .andExpect(status().isBadRequest());

        List<EmailSender> emailSenderList = emailSenderRepository.findAll();
        assertThat(emailSenderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEmailSenders() throws Exception {
        // Initialize the database
        emailSenderRepository.saveAndFlush(emailSender);

        // Get all the emailSenderList
        restEmailSenderMockMvc.perform(get("/api/email-senders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emailSender.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())));
    }
    
    @Test
    @Transactional
    public void getEmailSender() throws Exception {
        // Initialize the database
        emailSenderRepository.saveAndFlush(emailSender);

        // Get the emailSender
        restEmailSenderMockMvc.perform(get("/api/email-senders/{id}", emailSender.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(emailSender.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEmailSender() throws Exception {
        // Get the emailSender
        restEmailSenderMockMvc.perform(get("/api/email-senders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmailSender() throws Exception {
        // Initialize the database
        emailSenderRepository.saveAndFlush(emailSender);

        int databaseSizeBeforeUpdate = emailSenderRepository.findAll().size();

        // Update the emailSender
        EmailSender updatedEmailSender = emailSenderRepository.findById(emailSender.getId()).get();
        // Disconnect from session so that the updates on updatedEmailSender are not directly saved in db
        em.detach(updatedEmailSender);
        updatedEmailSender
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL);

        restEmailSenderMockMvc.perform(put("/api/email-senders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedEmailSender)))
            .andExpect(status().isOk());

        // Validate the EmailSender in the database
        List<EmailSender> emailSenderList = emailSenderRepository.findAll();
        assertThat(emailSenderList).hasSize(databaseSizeBeforeUpdate);
        EmailSender testEmailSender = emailSenderList.get(emailSenderList.size() - 1);
        assertThat(testEmailSender.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmailSender.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void updateNonExistingEmailSender() throws Exception {
        int databaseSizeBeforeUpdate = emailSenderRepository.findAll().size();

        // Create the EmailSender

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailSenderMockMvc.perform(put("/api/email-senders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(emailSender)))
            .andExpect(status().isBadRequest());

        // Validate the EmailSender in the database
        List<EmailSender> emailSenderList = emailSenderRepository.findAll();
        assertThat(emailSenderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEmailSender() throws Exception {
        // Initialize the database
        emailSenderRepository.saveAndFlush(emailSender);

        int databaseSizeBeforeDelete = emailSenderRepository.findAll().size();

        // Delete the emailSender
        restEmailSenderMockMvc.perform(delete("/api/email-senders/{id}", emailSender.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<EmailSender> emailSenderList = emailSenderRepository.findAll();
        assertThat(emailSenderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmailSender.class);
        EmailSender emailSender1 = new EmailSender();
        emailSender1.setId(1L);
        EmailSender emailSender2 = new EmailSender();
        emailSender2.setId(emailSender1.getId());
        assertThat(emailSender1).isEqualTo(emailSender2);
        emailSender2.setId(2L);
        assertThat(emailSender1).isNotEqualTo(emailSender2);
        emailSender1.setId(null);
        assertThat(emailSender1).isNotEqualTo(emailSender2);
    }
}
