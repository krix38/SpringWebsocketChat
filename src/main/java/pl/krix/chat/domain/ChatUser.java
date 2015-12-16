package pl.krix.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by krix on 12.11.15.
 */

@Entity
public class ChatUser {

    @Id
    @Column(unique=true)
    private String login;

    @JsonIgnore
    private String passwordHash;

    private String role;

    private Boolean enabled;

    public ChatUser(){};

    public ChatUser(String login, String passwordHash, String role, Boolean enabled) {
        this.passwordHash = passwordHash;
        this.login = login;
        this.role = role;
        this.enabled = enabled;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
