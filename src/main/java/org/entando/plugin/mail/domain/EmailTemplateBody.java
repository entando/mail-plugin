package org.entando.plugin.mail.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A EmailTemplateBody.
 */
@Entity
@Table(name = "email_template_body")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EmailTemplateBody implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "lang", nullable = false)
    private String lang;

    @NotNull
    @Column(name = "jhi_body", nullable = false)
    private String body;

    @ManyToOne
    @JsonIgnoreProperties("bodies")
    private EmailTemplate template;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public EmailTemplateBody lang(String lang) {
        this.lang = lang;
        return this;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getBody() {
        return body;
    }

    public EmailTemplateBody body(String body) {
        this.body = body;
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public EmailTemplate getTemplate() {
        return template;
    }

    public EmailTemplateBody template(EmailTemplate emailTemplate) {
        this.template = emailTemplate;
        return this;
    }

    public void setTemplate(EmailTemplate emailTemplate) {
        this.template = emailTemplate;
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
        EmailTemplateBody emailTemplateBody = (EmailTemplateBody) o;
        if (emailTemplateBody.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), emailTemplateBody.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EmailTemplateBody{" +
            "id=" + getId() +
            ", lang='" + getLang() + "'" +
            ", body='" + getBody() + "'" +
            "}";
    }
}
