/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fracturedatlas.athena.audit.model;

import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.fracturedatlas.athena.id.IdAdapter;
import org.hibernate.annotations.Type;

/**
 *
 * @author fintan
 */
@Entity
@Table(name = "MESSAGES")
public class AuditMessage {


    @Id
    @Type(type = "org.fracturedatlas.athena.audit.persist.impl.LongUserType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlJavaTypeAdapter(IdAdapter.class)
    Object id;

    Long dateTime;
    String User;
    String Action;
    String Resource;
    String Message;
    

    public AuditMessage() {
    }

    public AuditMessage(String User, String Action, String Resource, String Message) {
        this.id = null;
        Calendar cal = Calendar.getInstance();
        this.dateTime = System.currentTimeMillis();
        this.User = User;
        this.Action = Action;
        this.Resource = Resource;
        this.Message = Message;
    }

    public Long getDateTime() {
        return dateTime;
    }


    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateTime(String s) throws Exception {
        setDateTime(Long.getLong(s));
    }


      public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String Action) {
        this.Action = Action;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getResource() {
        return Resource;
    }

    public void setResource(String Resource) {
        this.Resource = Resource;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String User) {
        this.User = User;
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
        if (this.dateTime!=null) {
            if (other.dateTime!=null) {
                if (!this.dateTime.equals(other.dateTime)) {
                    return false;
                }
            } else {
                return false;
            }
        } else if (other.dateTime!=null) {
            return false;
        }

        if ((this.User == null) ? (other.User != null) : !this.User.equals(other.User)) {
            return false;
        }
        if ((this.Action == null) ? (other.Action != null) : !this.Action.equals(other.Action)) {
            return false;
        }
        if ((this.Resource == null) ? (other.Resource != null) : !this.Resource.equals(other.Resource)) {
            return false;
        }
        if ((this.Message == null) ? (other.Message != null) : !this.Message.equals(other.Message)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.dateTime != null ? this.dateTime.hashCode() : 0);
        hash = 97 * hash + (this.User != null ? this.User.hashCode() : 0);
        hash = 97 * hash + (this.Action != null ? this.Action.hashCode() : 0);
        hash = 97 * hash + (this.Resource != null ? this.Resource.hashCode() : 0);
        hash = 97 * hash + (this.Message != null ? this.Message.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "AuditMessage{" + "id=" + id + "dateTime=" + dateTime + "User=" + User + "Action=" + Action + "Resource=" + Resource + "Message=" + Message + '}';
    }

 
 

}
