package org.entando.plugin.mail.repository;

import org.entando.plugin.mail.domain.EmailTemplate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the EmailTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

}
