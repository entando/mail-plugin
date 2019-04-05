package org.entando.plugin.mail.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

import org.entando.plugin.mail.domain.enumeration.SmtpSecurity;

/**
 * A SmtpServerConfig.
 */
@Entity
@Table(name = "smtp_server_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SmtpServerConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "active")
    private boolean active;

    @Column(name = "jhi_debug")
    private boolean debug;

    @NotNull
    @Column(name = "host", nullable = false)
    private String host;

    @Column(name = "port")
    private Integer port;

    @Column(name = "timeout")
    private Integer timeout;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "security", nullable = false)
    private SmtpSecurity security;

    @Column(name = "username")
    private String username;

    @Column(name = "jhi_password")
    private String password;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public SmtpServerConfig active(boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDebug() {
        return debug;
    }

    public SmtpServerConfig debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getHost() {
        return host;
    }

    public SmtpServerConfig host(String host) {
        this.host = host;
        return this;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public SmtpServerConfig port(Integer port) {
        this.port = port;
        return this;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public SmtpServerConfig timeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public SmtpSecurity getSecurity() {
        return security;
    }

    public SmtpServerConfig security(SmtpSecurity security) {
        this.security = security;
        return this;
    }

    public void setSecurity(SmtpSecurity security) {
        this.security = security;
    }

    public String getUsername() {
        return username;
    }

    public SmtpServerConfig username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public SmtpServerConfig password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove
  
    /**
     * Return true if mail configuration expects an anonymous
     * authentication.<br/>
     * NOTE: an anonymous authentication occurs whenever the username and the
     * associated password are <b>not</b> provided <b>and</b> no security
     * encapsulation protocol is specified.
     *
     * @return True if the username and the password are not provided
     */
    public boolean hasAnonimousAuth() {
        return ((StringUtils.isEmpty(username) && StringUtils.isEmpty(password))
                && security == SmtpSecurity.NONE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SmtpServerConfig smtpServerConfig = (SmtpServerConfig) o;
        if (smtpServerConfig.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), smtpServerConfig.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SmtpServerConfig{" +
            "id=" + getId() +
            ", active='" + isActive() + "'" +
            ", debug='" + isDebug() + "'" +
            ", host='" + getHost() + "'" +
            ", port=" + getPort() +
            ", timeout=" + getTimeout() +
            ", security='" + getSecurity() + "'" +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
