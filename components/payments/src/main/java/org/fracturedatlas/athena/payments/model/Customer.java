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
package org.fracturedatlas.athena.payments.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Customer {

    String firstName;
    String lastName;
    String company;
    String phone;
    String fax;
    String email;
    String id;
    List<CreditCard> creditCards = new ArrayList();

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Customer other = (Customer) obj;
        if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if ((this.company == null) ? (other.company != null) : !this.company.equals(other.company)) {
            return false;
        }
        if ((this.phone == null) ? (other.phone != null) : !this.phone.equals(other.phone)) {
            return false;
        }
        if ((this.fax == null) ? (other.fax != null) : !this.fax.equals(other.fax)) {
            return false;
        }
        if ((this.email == null) ? (other.email != null) : !this.email.equals(other.email)) {
            return false;
        }
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 79 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 79 * hash + (this.company != null ? this.company.hashCode() : 0);
        hash = 79 * hash + (this.phone != null ? this.phone.hashCode() : 0);
        hash = 79 * hash + (this.fax != null ? this.fax.hashCode() : 0);
        hash = 79 * hash + (this.email != null ? this.email.hashCode() : 0);
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public String toEscapedString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);

        builder.append(firstName).append(lastName).append(company).append(phone).append(fax).append(email).append(id);

        for (CreditCard cc : creditCards) {
            builder.append(cc.toEscapedString());
        }
        return builder.toString();
    }
}
