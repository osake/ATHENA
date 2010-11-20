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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class CreditCard {

    String cardNumber;
    String expirationDate;
    String cardholderName;
    String cvv;
    String token;
    String id;
    Customer customer;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public static String lastFour(String cardNumber) {
        return StringUtils.right(cardNumber, 4);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CreditCard other = (CreditCard) obj;
        if ((this.cardNumber == null) ? (other.cardNumber != null) : !this.cardNumber.equals(other.cardNumber)) {
            return false;
        }
        if ((this.expirationDate == null) ? (other.expirationDate != null) : !this.expirationDate.equals(other.expirationDate)) {
            return false;
        }
        if ((this.cardholderName == null) ? (other.cardholderName != null) : !this.cardholderName.equals(other.cardholderName)) {
            return false;
        }
        if ((this.cvv == null) ? (other.cvv != null) : !this.cvv.equals(other.cvv)) {
            return false;
        }
        if ((this.token == null) ? (other.token != null) : !this.token.equals(other.token)) {
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
        hash = 43 * hash + (this.cardNumber != null ? this.cardNumber.hashCode() : 0);
        hash = 43 * hash + (this.expirationDate != null ? this.expirationDate.hashCode() : 0);
        hash = 43 * hash + (this.cardholderName != null ? this.cardholderName.hashCode() : 0);
        hash = 43 * hash + (this.cvv != null ? this.cvv.hashCode() : 0);
        hash = 43 * hash + (this.token != null ? this.token.hashCode() : 0);
        hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        return builder.append(id).append(cardNumber).append(expirationDate).append(cardholderName).append(token).toString();
    }
}
