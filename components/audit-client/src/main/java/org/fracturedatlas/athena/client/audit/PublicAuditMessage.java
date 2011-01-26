/*

ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/

 */
package org.fracturedatlas.athena.client.audit;




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
