package org.entando.plugin.mail.web.rest;
import org.entando.plugin.mail.domain.EmailTemplateBody;
import org.entando.plugin.mail.repository.EmailTemplateBodyRepository;
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
 * REST controller for managing EmailTemplateBody.
 */
@RestController
@RequestMapping("/api")
public class EmailTemplateBodyResource {

    private final Logger log = LoggerFactory.getLogger(EmailTemplateBodyResource.class);

    private static final String ENTITY_NAME = "entandoMailPluginEmailTemplateBody";

    private final EmailTemplateBodyRepository emailTemplateBodyRepository;

    public EmailTemplateBodyResource(EmailTemplateBodyRepository emailTemplateBodyRepository) {
        this.emailTemplateBodyRepository = emailTemplateBodyRepository;
    }

    /**
     * POST  /email-template-bodies : Create a new emailTemplateBody.
     *
     * @param emailTemplateBody the emailTemplateBody to create
     * @return the ResponseEntity with status 201 (Created) and with body the new emailTemplateBody, or with status 400 (Bad Request) if the emailTemplateBody has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/email-template-bodies")
    public ResponseEntity<EmailTemplateBody> createEmailTemplateBody(@Valid @RequestBody EmailTemplateBody emailTemplateBody) throws URISyntaxException {
        log.debug("REST request to save EmailTemplateBody : {}", emailTemplateBody);
        if (emailTemplateBody.getId() != null) {
            throw new BadRequestAlertException("A new emailTemplateBody cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EmailTemplateBody result = emailTemplateBodyRepository.save(emailTemplateBody);
        return ResponseEntity.created(new URI("/api/email-template-bodies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /email-template-bodies : Updates an existing emailTemplateBody.
     *
     * @param emailTemplateBody the emailTemplateBody to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated emailTemplateBody,
     * or with status 400 (Bad Request) if the emailTemplateBody is not valid,
     * or with status 500 (Internal Server Error) if the emailTemplateBody couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/email-template-bodies")
    public ResponseEntity<EmailTemplateBody> updateEmailTemplateBody(@Valid @RequestBody EmailTemplateBody emailTemplateBody) throws URISyntaxException {
        log.debug("REST request to update EmailTemplateBody : {}", emailTemplateBody);
        if (emailTemplateBody.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EmailTemplateBody result = emailTemplateBodyRepository.save(emailTemplateBody);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, emailTemplateBody.getId().toString()))
            .body(result);
    }

    /**
     * GET  /email-template-bodies : get all the emailTemplateBodies.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of emailTemplateBodies in body
     */
    @GetMapping("/email-template-bodies")
    public List<EmailTemplateBody> getAllEmailTemplateBodies() {
        log.debug("REST request to get all EmailTemplateBodies");
        return emailTemplateBodyRepository.findAll();
    }

    /**
     * GET  /email-template-bodies/:id : get the "id" emailTemplateBody.
     *
     * @param id the id of the emailTemplateBody to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the emailTemplateBody, or with status 404 (Not Found)
     */
    @GetMapping("/email-template-bodies/{id}")
    public ResponseEntity<EmailTemplateBody> getEmailTemplateBody(@PathVariable Long id) {
        log.debug("REST request to get EmailTemplateBody : {}", id);
        Optional<EmailTemplateBody> emailTemplateBody = emailTemplateBodyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(emailTemplateBody);
    }

    /**
     * DELETE  /email-template-bodies/:id : delete the "id" emailTemplateBody.
     *
     * @param id the id of the emailTemplateBody to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/email-template-bodies/{id}")
    public ResponseEntity<Void> deleteEmailTemplateBody(@PathVariable Long id) {
        log.debug("REST request to delete EmailTemplateBody : {}", id);
        emailTemplateBodyRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
