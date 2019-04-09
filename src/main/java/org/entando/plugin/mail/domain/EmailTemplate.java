package org.entando.plugin.mail.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A EmailTemplate.
 */
@Entity
@Table(name = "email_template")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EmailTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @OneToMany(mappedBy = "template")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<EmailTemplateBody> bodies = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public EmailTemplate name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public EmailTemplate subject(String subject) {
        this.subject = subject;
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Set<EmailTemplateBody> getBodies() {
        return bodies;
    }

    public EmailTemplate bodies(Set<EmailTemplateBody> emailTemplateBodies) {
        this.bodies = emailTemplateBodies;
        return this;
    }

    public EmailTemplate addBody(EmailTemplateBody emailTemplateBody) {
        this.bodies.add(emailTemplateBody);
        emailTemplateBody.setTemplate(this);
        return this;
    }

    public EmailTemplate removeBody(EmailTemplateBody emailTemplateBody) {
        this.bodies.remove(emailTemplateBody);
        emailTemplateBody.setTemplate(null);
        return this;
    }

    public void setBodies(Set<EmailTemplateBody> emailTemplateBodies) {
        this.bodies = emailTemplateBodies;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailTemplate emailTemplate = (EmailTemplate) o;
        if (emailTemplate.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), emailTemplate.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EmailTemplate{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", subject='" + getSubject() + "'" +
            "}";
    }
}
