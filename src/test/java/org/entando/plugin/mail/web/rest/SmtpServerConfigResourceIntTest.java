package org.entando.plugin.mail.web.rest;

import org.entando.plugin.mail.MailPluginApp;

import org.entando.plugin.mail.domain.SmtpServerConfig;
import org.entando.plugin.mail.repository.SmtpServerConfigRepository;
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

import org.entando.plugin.mail.domain.enumeration.SmtpSecurity;
/**
 * Test class for the SmtpServerConfigResource REST controller.
 *
 * @see SmtpServerConfigResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailPluginApp.class)
public class SmtpServerConfigResourceIntTest {

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Boolean DEFAULT_DEBUG = false;
    private static final Boolean UPDATED_DEBUG = true;

    private static final String DEFAULT_HOST = "AAAAAAAAAA";
    private static final String UPDATED_HOST = "BBBBBBBBBB";

    private static final Integer DEFAULT_PORT = 1;
    private static final Integer UPDATED_PORT = 2;

    private static final Integer DEFAULT_TIMEOUT = 1;
    private static final Integer UPDATED_TIMEOUT = 2;

    private static final SmtpSecurity DEFAULT_SECURITY = SmtpSecurity.NONE;
    private static final SmtpSecurity UPDATED_SECURITY = SmtpSecurity.SSL;

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    @Autowired
    private SmtpServerConfigRepository smtpServerConfigRepository;

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

    private MockMvc restSmtpServerConfigMockMvc;

    private SmtpServerConfig smtpServerConfig;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SmtpServerConfigResource smtpServerConfigResource = new SmtpServerConfigResource(smtpServerConfigRepository);
        this.restSmtpServerConfigMockMvc = MockMvcBuilders.standaloneSetup(smtpServerConfigResource)
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
    public static SmtpServerConfig createEntity(EntityManager em) {
        SmtpServerConfig smtpServerConfig = new SmtpServerConfig()
            .active(DEFAULT_ACTIVE)
            .debug(DEFAULT_DEBUG)
            .host(DEFAULT_HOST)
            .port(DEFAULT_PORT)
            .timeout(DEFAULT_TIMEOUT)
            .security(DEFAULT_SECURITY)
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD);
        return smtpServerConfig;
    }

    @Before
    public void initTest() {
        smtpServerConfig = createEntity(em);
    }

    @Test
    @Transactional
    public void createSmtpServerConfig() throws Exception {
        int databaseSizeBeforeCreate = smtpServerConfigRepository.findAll().size();

        // Create the SmtpServerConfig
        restSmtpServerConfigMockMvc.perform(post("/api/smtp-server-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(smtpServerConfig)))
            .andExpect(status().isCreated());

        // Validate the SmtpServerConfig in the database
        List<SmtpServerConfig> smtpServerConfigList = smtpServerConfigRepository.findAll();
        assertThat(smtpServerConfigList).hasSize(databaseSizeBeforeCreate + 1);
        SmtpServerConfig testSmtpServerConfig = smtpServerConfigList.get(smtpServerConfigList.size() - 1);
        assertThat(testSmtpServerConfig.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testSmtpServerConfig.isDebug()).isEqualTo(DEFAULT_DEBUG);
        assertThat(testSmtpServerConfig.getHost()).isEqualTo(DEFAULT_HOST);
        assertThat(testSmtpServerConfig.getPort()).isEqualTo(DEFAULT_PORT);
        assertThat(testSmtpServerConfig.getTimeout()).isEqualTo(DEFAULT_TIMEOUT);
        assertThat(testSmtpServerConfig.getSecurity()).isEqualTo(DEFAULT_SECURITY);
        assertThat(testSmtpServerConfig.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testSmtpServerConfig.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    public void createSmtpServerConfigWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = smtpServerConfigRepository.findAll().size();

        // Create the SmtpServerConfig with an existing ID
        smtpServerConfig.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSmtpServerConfigMockMvc.perform(post("/api/smtp-server-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(smtpServerConfig)))
            .andExpect(status().isBadRequest());

        // Validate the SmtpServerConfig in the database
        List<SmtpServerConfig> smtpServerConfigList = smtpServerConfigRepository.findAll();
        assertThat(smtpServerConfigList).hasSize(databaseSizeBeforeCreate);
    }
    
    @Test
    @Transactional
    public void createSmtpServerConfigWhenAlreadyConfigured() throws Exception {

        SmtpServerConfig serverConfig = new SmtpServerConfig();

        serverConfig.setHost("localhost");
        serverConfig.setPort(2525);
        serverConfig.setActive(true);
        serverConfig.setDebug(false);
        serverConfig.setSecurity(SmtpSecurity.NONE);

        smtpServerConfigRepository.save(serverConfig);

        int databaseSizeBeforeCreate = smtpServerConfigRepository.findAll().size();

        // There must be at most one SMTP server configuration
        restSmtpServerConfigMockMvc.perform(post("/api/smtp-server-configs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(smtpServerConfig)))
                .andExpect(status().isBadRequest());

        // Validate the SmtpServerConfig in the database
        List<SmtpServerConfig> smtpServerConfigList = smtpServerConfigRepository.findAll();
        assertThat(smtpServerConfigList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkHostIsRequired() throws Exception {
        int databaseSizeBeforeTest = smtpServerConfigRepository.findAll().size();
        // set the field null
        smtpServerConfig.setHost(null);

        // Create the SmtpServerConfig, which fails.

        restSmtpServerConfigMockMvc.perform(post("/api/smtp-server-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(smtpServerConfig)))
            .andExpect(status().isBadRequest());

        List<SmtpServerConfig> smtpServerConfigList = smtpServerConfigRepository.findAll();
        assertThat(smtpServerConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSecurityIsRequired() throws Exception {
        int databaseSizeBeforeTest = smtpServerConfigRepository.findAll().size();
        // set the field null
        smtpServerConfig.setSecurity(null);

        // Create the SmtpServerConfig, which fails.

        restSmtpServerConfigMockMvc.perform(post("/api/smtp-server-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(smtpServerConfig)))
            .andExpect(status().isBadRequest());

        List<SmtpServerConfig> smtpServerConfigList = smtpServerConfigRepository.findAll();
        assertThat(smtpServerConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSmtpServerConfigs() throws Exception {
        // Initialize the database
        smtpServerConfigRepository.saveAndFlush(smtpServerConfig);

        // Get all the smtpServerConfigList
        restSmtpServerConfigMockMvc.perform(get("/api/smtp-server-configs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(smtpServerConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].debug").value(hasItem(DEFAULT_DEBUG.booleanValue())))
            .andExpect(jsonPath("$.[*].host").value(hasItem(DEFAULT_HOST.toString())))
            .andExpect(jsonPath("$.[*].port").value(hasItem(DEFAULT_PORT)))
            .andExpect(jsonPath("$.[*].timeout").value(hasItem(DEFAULT_TIMEOUT)))
            .andExpect(jsonPath("$.[*].security").value(hasItem(DEFAULT_SECURITY.toString())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())));
    }
    
    @Test
    @Transactional
    public void getSmtpServerConfig() throws Exception {
        // Initialize the database
        smtpServerConfigRepository.saveAndFlush(smtpServerConfig);

        // Get the smtpServerConfig
        restSmtpServerConfigMockMvc.perform(get("/api/smtp-server-configs/{id}", smtpServerConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(smtpServerConfig.getId().intValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.debug").value(DEFAULT_DEBUG.booleanValue()))
            .andExpect(jsonPath("$.host").value(DEFAULT_HOST.toString()))
            .andExpect(jsonPath("$.port").value(DEFAULT_PORT))
            .andExpect(jsonPath("$.timeout").value(DEFAULT_TIMEOUT))
            .andExpect(jsonPath("$.security").value(DEFAULT_SECURITY.toString()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSmtpServerConfig() throws Exception {
        // Get the smtpServerConfig
        restSmtpServerConfigMockMvc.perform(get("/api/smtp-server-configs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSmtpServerConfig() throws Exception {
        // Initialize the database
        smtpServerConfigRepository.saveAndFlush(smtpServerConfig);

        int databaseSizeBeforeUpdate = smtpServerConfigRepository.findAll().size();

        // Update the smtpServerConfig
        SmtpServerConfig updatedSmtpServerConfig = smtpServerConfigRepository.findById(smtpServerConfig.getId()).get();
        // Disconnect from session so that the updates on updatedSmtpServerConfig are not directly saved in db
        em.detach(updatedSmtpServerConfig);
        updatedSmtpServerConfig
            .active(UPDATED_ACTIVE)
            .debug(UPDATED_DEBUG)
            .host(UPDATED_HOST)
            .port(UPDATED_PORT)
            .timeout(UPDATED_TIMEOUT)
            .security(UPDATED_SECURITY)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD);

        restSmtpServerConfigMockMvc.perform(put("/api/smtp-server-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSmtpServerConfig)))
            .andExpect(status().isOk());

        // Validate the SmtpServerConfig in the database
        List<SmtpServerConfig> smtpServerConfigList = smtpServerConfigRepository.findAll();
        assertThat(smtpServerConfigList).hasSize(databaseSizeBeforeUpdate);
        SmtpServerConfig testSmtpServerConfig = smtpServerConfigList.get(smtpServerConfigList.size() - 1);
        assertThat(testSmtpServerConfig.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testSmtpServerConfig.isDebug()).isEqualTo(UPDATED_DEBUG);
        assertThat(testSmtpServerConfig.getHost()).isEqualTo(UPDATED_HOST);
        assertThat(testSmtpServerConfig.getPort()).isEqualTo(UPDATED_PORT);
        assertThat(testSmtpServerConfig.getTimeout()).isEqualTo(UPDATED_TIMEOUT);
        assertThat(testSmtpServerConfig.getSecurity()).isEqualTo(UPDATED_SECURITY);
        assertThat(testSmtpServerConfig.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testSmtpServerConfig.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void updateNonExistingSmtpServerConfig() throws Exception {
        int databaseSizeBeforeUpdate = smtpServerConfigRepository.findAll().size();

        // Create the SmtpServerConfig

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSmtpServerConfigMockMvc.perform(put("/api/smtp-server-configs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(smtpServerConfig)))
            .andExpect(status().isBadRequest());

        // Validate the SmtpServerConfig in the database
        List<SmtpServerConfig> smtpServerConfigList = smtpServerConfigRepository.findAll();
        assertThat(smtpServerConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSmtpServerConfig() throws Exception {
        // Initialize the database
        smtpServerConfigRepository.saveAndFlush(smtpServerConfig);

        int databaseSizeBeforeDelete = smtpServerConfigRepository.findAll().size();

        // Delete the smtpServerConfig
        restSmtpServerConfigMockMvc.perform(delete("/api/smtp-server-configs/{id}", smtpServerConfig.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SmtpServerConfig> smtpServerConfigList = smtpServerConfigRepository.findAll();
        assertThat(smtpServerConfigList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SmtpServerConfig.class);
        SmtpServerConfig smtpServerConfig1 = new SmtpServerConfig();
        smtpServerConfig1.setId(1L);
        SmtpServerConfig smtpServerConfig2 = new SmtpServerConfig();
        smtpServerConfig2.setId(smtpServerConfig1.getId());
        assertThat(smtpServerConfig1).isEqualTo(smtpServerConfig2);
        smtpServerConfig2.setId(2L);
        assertThat(smtpServerConfig1).isNotEqualTo(smtpServerConfig2);
        smtpServerConfig1.setId(null);
        assertThat(smtpServerConfig1).isNotEqualTo(smtpServerConfig2);
    }
}
