package org.entando.plugin.mail.repository;

import java.util.Optional;
import org.entando.plugin.mail.domain.EmailSender;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the EmailSender entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmailSenderRepository extends JpaRepository<EmailSender, Long> {

    Optional<EmailSender> findByName(String name);
}
