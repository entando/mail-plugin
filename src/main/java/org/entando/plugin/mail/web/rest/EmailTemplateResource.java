package org.entando.plugin.mail.web.rest;
import org.entando.plugin.mail.domain.EmailTemplate;
import org.entando.plugin.mail.repository.EmailTemplateRepository;
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
 * REST controller for managing EmailTemplate.
 */
@RestController
@RequestMapping("/api")
public class EmailTemplateResource {

    private final Logger log = LoggerFactory.getLogger(EmailTemplateResource.class);

    private static final String ENTITY_NAME = "mailPluginEmailTemplate";

    private final EmailTemplateRepository emailTemplateRepository;

    public EmailTemplateResource(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
    }

    /**
     * POST  /email-templates : Create a new emailTemplate.
     *
     * @param emailTemplate the emailTemplate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new emailTemplate, or with status 400 (Bad Request) if the emailTemplate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/email-templates")
    public ResponseEntity<EmailTemplate> createEmailTemplate(@Valid @RequestBody EmailTemplate emailTemplate) throws URISyntaxException {
        log.debug("REST request to save EmailTemplate : {}", emailTemplate);
        if (emailTemplate.getId() != null) {
            throw new BadRequestAlertException("A new emailTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (emailTemplateRepository.findByName(emailTemplate.getName()).isPresent()) {
            throw new BadRequestAlertException("An emailTemplate named " + emailTemplate.getName() + " already exists", ENTITY_NAME, "nameexists");
        }
        EmailTemplate result = emailTemplateRepository.save(emailTemplate);
        return ResponseEntity.created(new URI("/api/email-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /email-templates : Updates an existing emailTemplate.
     *
     * @param emailTemplate the emailTemplate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated emailTemplate,
     * or with status 400 (Bad Request) if the emailTemplate is not valid,
     * or with status 500 (Internal Server Error) if the emailTemplate couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/email-templates")
    public ResponseEntity<EmailTemplate> updateEmailTemplate(@Valid @RequestBody EmailTemplate emailTemplate) throws URISyntaxException {
        log.debug("REST request to update EmailTemplate : {}", emailTemplate);
        if (emailTemplate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EmailTemplate result = emailTemplateRepository.save(emailTemplate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, emailTemplate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /email-templates : get all the emailTemplates.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of emailTemplates in body
     */
    @GetMapping("/email-templates")
    public List<EmailTemplate> getAllEmailTemplates() {
        log.debug("REST request to get all EmailTemplates");
        return emailTemplateRepository.findAll();
    }

    /**
     * GET  /email-templates/:id : get the "id" emailTemplate.
     *
     * @param id the id of the emailTemplate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the emailTemplate, or with status 404 (Not Found)
     */
    @GetMapping("/email-templates/{id}")
    public ResponseEntity<EmailTemplate> getEmailTemplate(@PathVariable Long id) {
        log.debug("REST request to get EmailTemplate : {}", id);
        Optional<EmailTemplate> emailTemplate = emailTemplateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(emailTemplate);
    }

    /**
     * DELETE  /email-templates/:id : delete the "id" emailTemplate.
     *
     * @param id the id of the emailTemplate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/email-templates/{id}")
    public ResponseEntity<Void> deleteEmailTemplate(@PathVariable Long id) {
        log.debug("REST request to delete EmailTemplate : {}", id);
        emailTemplateRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
