package org.entando.plugin.mail.repository;

import org.entando.plugin.mail.domain.EmailTemplateBody;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the EmailTemplateBody entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmailTemplateBodyRepository extends JpaRepository<EmailTemplateBody, Long> {

}
