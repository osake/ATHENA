/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.audit.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.fracturedatlas.athena.client.audit.PublicAuditMessage;

/**
 *
 * @author fintan
 */
@Entity
@Table(name = "MESSAGES")
public class AuditMessage {

    public AuditMessage(PublicAuditMessage auditMessage) {
        this.setId(auditMessage.getId());
        this.setDateTime(auditMessage.getDateTime());
        this.setUser(auditMessage.getUser());
        this.setAction(auditMessage.getAction());
        this.setResource(auditMessage.getResource());
        this.setMessage(auditMessage.getMessage());

    }

    public PublicAuditMessage toPublicMessage() {
        PublicAuditMessage pam = new PublicAuditMessage();
        pam.setId(this.id);
        pam.setDateTime(this.dateTime);
        pam.setUser(this.user);
        pam.setAction(this.action);
        pam.setResource(this.resource);
        pam.setMessage(this.message);
        return pam;
    }

    public enum Variable {

        ID, DATETIME, USER, ACTION,
        RESOURCE, MESSAGE, NOVALUE;

        public static Variable toVariable(String str) {
            try {
                return valueOf(str.toUpperCase());
            } catch (Exception ex) {
                return NOVALUE;
            }
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Long dateTime;
    String user;
    String action;
    String resource;
    String message;

    public AuditMessage() {
    }

    public AuditMessage(String User, String Action, String Resource, String Message) {
        this.id = null;
        this.dateTime = System.currentTimeMillis();
        this.user = User;
        this.action = Action;
        this.resource = Resource;
        this.message = Message;
    }

    public Object get(String variable) {
        switch (Variable.toVariable(variable)) {
            case ID:
                return getId();
            case DATETIME:
                return getDateTime();
            case USER:
                return getUser();
            case ACTION:
                return getAction();
            case RESOURCE:
                return getResource();
            case MESSAGE:
                return getMessage();
        }
        return null;
    }

    public void set(String variable, String value) {
        switch (Variable.toVariable(variable)) {
            case ID:
                setId(value);
                break;
            case DATETIME:
                try {
                    setDateTime(value);
                } catch (Exception ex) {
                    Logger.getLogger(AuditMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case USER:
                setUser(value);
                break;
            case ACTION:
                setAction(value);
                break;
            case RESOURCE:
                setResource(value);
                break;
            case MESSAGE:
                setMessage(value);
        }
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateTime(String s) throws Exception {
        setDateTime(Long.valueOf(s));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = Long.valueOf(id);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String Action) {
        this.action = Action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String Message) {
        this.message = Message;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String Resource) {
        this.resource = Resource;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String User) {
        this.user = User;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuditMessage other = (AuditMessage) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.dateTime != null) {
            if (other.dateTime != null) {
                if (!this.dateTime.equals(other.dateTime)) {
                    return false;
                }
            } else {
                return false;
            }
        } else if (other.dateTime != null) {
            return false;
        }

        if ((this.user == null) ? (other.user != null) : !this.user.equals(other.user)) {
            return false;
        }
        if ((this.action == null) ? (other.action != null) : !this.action.equals(other.action)) {
            return false;
        }
        if ((this.resource == null) ? (other.resource != null) : !this.resource.equals(other.resource)) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : !this.message.equals(other.message)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.dateTime != null ? this.dateTime.hashCode() : 0);
        hash = 97 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 97 * hash + (this.action != null ? this.action.hashCode() : 0);
        hash = 97 * hash + (this.resource != null ? this.resource.hashCode() : 0);
        hash = 97 * hash + (this.message != null ? this.message.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "AuditMessage{" + "id=" + id + "dateTime=" + dateTime + "User=" + user + "Action=" + action + "Resource=" + resource + "Message=" + message + '}';
    }
}
