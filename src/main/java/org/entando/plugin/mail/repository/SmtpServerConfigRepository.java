package org.entando.plugin.mail.repository;

import org.entando.plugin.mail.domain.SmtpServerConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the SmtpServerConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SmtpServerConfigRepository extends JpaRepository<SmtpServerConfig, Long> {

}
