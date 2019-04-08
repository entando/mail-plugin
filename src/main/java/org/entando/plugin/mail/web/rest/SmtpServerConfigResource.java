package org.entando.plugin.mail.web.rest;
import org.entando.plugin.mail.domain.SmtpServerConfig;
import org.entando.plugin.mail.repository.SmtpServerConfigRepository;
import org.entando.plugin.mail.web.rest.errors.BadRequestAlertException;
import org.entando.plugin.mail.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SmtpServerConfig.
 */
@RestController
@RequestMapping("/api")
public class SmtpServerConfigResource {

    private final Logger log = LoggerFactory.getLogger(SmtpServerConfigResource.class);

    private static final String ENTITY_NAME = "entandoMailPluginSmtpServerConfig";

    private final SmtpServerConfigRepository smtpServerConfigRepository;

    public SmtpServerConfigResource(SmtpServerConfigRepository smtpServerConfigRepository) {
        this.smtpServerConfigRepository = smtpServerConfigRepository;
    }

    /**
     * POST  /smtp-server-configs : Create a new smtpServerConfig.
     *
     * @param smtpServerConfig the smtpServerConfig to create
     * @return the ResponseEntity with status 201 (Created) and with body the new smtpServerConfig, or with status 400 (Bad Request) if the smtpServerConfig has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/smtp-server-configs")
    public ResponseEntity<SmtpServerConfig> createSmtpServerConfig(@Valid @RequestBody SmtpServerConfig smtpServerConfig) throws URISyntaxException {
        log.debug("REST request to save SmtpServerConfig : {}", smtpServerConfig);
        if (smtpServerConfig.getId() != null) {
            throw new BadRequestAlertException("A new smtpServerConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        if (!smtpServerConfigRepository.findAll().isEmpty()) {
            throw new BadRequestAlertException("SMTP server already configured. Please DELETE the configuration or use the PUT HTTP method", ENTITY_NAME, "configexists");
        }
        
        SmtpServerConfig result = smtpServerConfigRepository.save(smtpServerConfig);
        return ResponseEntity.created(new URI("/api/smtp-server-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /smtp-server-configs : Updates an existing smtpServerConfig.
     *
     * @param smtpServerConfig the smtpServerConfig to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated smtpServerConfig,
     * or with status 400 (Bad Request) if the smtpServerConfig is not valid,
     * or with status 500 (Internal Server Error) if the smtpServerConfig couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/smtp-server-configs")
    public ResponseEntity<SmtpServerConfig> updateSmtpServerConfig(@Valid @RequestBody SmtpServerConfig smtpServerConfig) throws URISyntaxException {
        log.debug("REST request to update SmtpServerConfig : {}", smtpServerConfig);
        if (smtpServerConfig.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SmtpServerConfig result = smtpServerConfigRepository.save(smtpServerConfig);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, smtpServerConfig.getId().toString()))
            .body(result);
    }

    /**
     * GET  /smtp-server-configs : get all the smtpServerConfigs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of smtpServerConfigs in body
     */
    @GetMapping("/smtp-server-configs")
    public List<SmtpServerConfig> getAllSmtpServerConfigs() {
        log.debug("REST request to get all SmtpServerConfigs");
        return smtpServerConfigRepository.findAll();
    }

    /**
     * GET  /smtp-server-configs/:id : get the "id" smtpServerConfig.
     *
     * @param id the id of the smtpServerConfig to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the smtpServerConfig, or with status 404 (Not Found)
     */
    @GetMapping("/smtp-server-configs/{id}")
    public ResponseEntity<SmtpServerConfig> getSmtpServerConfig(@PathVariable Long id) {
        log.debug("REST request to get SmtpServerConfig : {}", id);
        Optional<SmtpServerConfig> smtpServerConfig = smtpServerConfigRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(smtpServerConfig);
    }

    /**
     * DELETE  /smtp-server-configs/:id : delete the "id" smtpServerConfig.
     *
     * @param id the id of the smtpServerConfig to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/smtp-server-configs/{id}")
    public ResponseEntity<Void> deleteSmtpServerConfig(@PathVariable Long id) {
        log.debug("REST request to delete SmtpServerConfig : {}", id);
        smtpServerConfigRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
