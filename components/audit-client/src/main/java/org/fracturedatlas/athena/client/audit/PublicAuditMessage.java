/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.client.audit;

/**
 *
 * @author fintan
 */


public class PublicAuditMessage {

    Long id;
    Long dateTime;
    String user;
    String action;
    String resource;
    String message;

    public PublicAuditMessage() {
    }

    public PublicAuditMessage(String user, String action, String resource, String message) {
        this.id = null;
        this.dateTime = System.currentTimeMillis();
        this.user = user;
        this.action = action;
        this.resource = resource;
        this.message = message;
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
    public String toString() {
        return "AuditMessage{" + "id=" + id + "dateTime=" + dateTime + "User=" + user + "Action=" + action + "Resource=" + resource + "Message=" + message + '}';
    }
}
