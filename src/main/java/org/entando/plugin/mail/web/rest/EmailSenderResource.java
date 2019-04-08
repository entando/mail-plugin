package org.entando.plugin.mail.web.rest;
import org.entando.plugin.mail.domain.EmailSender;
import org.entando.plugin.mail.repository.EmailSenderRepository;
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
 * REST controller for managing EmailSender.
 */
@RestController
@RequestMapping("/api")
public class EmailSenderResource {

    private final Logger log = LoggerFactory.getLogger(EmailSenderResource.class);

    private static final String ENTITY_NAME = "entandoMailPluginEmailSender";

    private final EmailSenderRepository emailSenderRepository;

    public EmailSenderResource(EmailSenderRepository emailSenderRepository) {
        this.emailSenderRepository = emailSenderRepository;
    }

    /**
     * POST  /email-senders : Create a new emailSender.
     *
     * @param emailSender the emailSender to create
     * @return the ResponseEntity with status 201 (Created) and with body the new emailSender, or with status 400 (Bad Request) if the emailSender has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/email-senders")
    public ResponseEntity<EmailSender> createEmailSender(@Valid @RequestBody EmailSender emailSender) throws URISyntaxException {
        log.debug("REST request to save EmailSender : {}", emailSender);
        if (emailSender.getId() != null) {
            throw new BadRequestAlertException("A new emailSender cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (emailSenderRepository.findByName(emailSender.getName()).isPresent()) {
            throw new BadRequestAlertException("An emailSender named " + emailSender.getName() + " already exists", ENTITY_NAME, "nameexists");
        }
        EmailSender result = emailSenderRepository.save(emailSender);
        return ResponseEntity.created(new URI("/api/email-senders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /email-senders : Updates an existing emailSender.
     *
     * @param emailSender the emailSender to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated emailSender,
     * or with status 400 (Bad Request) if the emailSender is not valid,
     * or with status 500 (Internal Server Error) if the emailSender couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/email-senders")
    public ResponseEntity<EmailSender> updateEmailSender(@Valid @RequestBody EmailSender emailSender) throws URISyntaxException {
        log.debug("REST request to update EmailSender : {}", emailSender);
        if (emailSender.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EmailSender result = emailSenderRepository.save(emailSender);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, emailSender.getId().toString()))
            .body(result);
    }

    /**
     * GET  /email-senders : get all the emailSenders.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of emailSenders in body
     */
    @GetMapping("/email-senders")
    public List<EmailSender> getAllEmailSenders() {
        log.debug("REST request to get all EmailSenders");
        return emailSenderRepository.findAll();
    }

    /**
     * GET  /email-senders/:id : get the "id" emailSender.
     *
     * @param id the id of the emailSender to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the emailSender, or with status 404 (Not Found)
     */
    @GetMapping("/email-senders/{id}")
    public ResponseEntity<EmailSender> getEmailSender(@PathVariable Long id) {
        log.debug("REST request to get EmailSender : {}", id);
        Optional<EmailSender> emailSender = emailSenderRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(emailSender);
    }

    /**
     * DELETE  /email-senders/:id : delete the "id" emailSender.
     *
     * @param id the id of the emailSender to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/email-senders/{id}")
    public ResponseEntity<Void> deleteEmailSender(@PathVariable Long id) {
        log.debug("REST request to delete EmailSender : {}", id);
        emailSenderRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
