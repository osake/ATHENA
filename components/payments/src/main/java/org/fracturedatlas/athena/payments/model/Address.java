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


public class Address {

    String firstName;
    String lastName;
    String company;
    String streetAddress1;
    String streetAddress2;
    String city;
    String state;
    String postalCode;
    String country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Address other = (Address) obj;
        if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if ((this.company == null) ? (other.company != null) : !this.company.equals(other.company)) {
            return false;
        }
        if ((this.streetAddress1 == null) ? (other.streetAddress1 != null) : !this.streetAddress1.equals(other.streetAddress1)) {
            return false;
        }
        if ((this.streetAddress2 == null) ? (other.streetAddress2 != null) : !this.streetAddress2.equals(other.streetAddress2)) {
            return false;
        }
        if ((this.city == null) ? (other.city != null) : !this.city.equals(other.city)) {
            return false;
        }
        if ((this.state == null) ? (other.state != null) : !this.state.equals(other.state)) {
            return false;
        }
        if ((this.postalCode == null) ? (other.postalCode != null) : !this.postalCode.equals(other.postalCode)) {
            return false;
        }
        if ((this.country == null) ? (other.country != null) : !this.country.equals(other.country)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 37 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 37 * hash + (this.company != null ? this.company.hashCode() : 0);
        hash = 37 * hash + (this.streetAddress1 != null ? this.streetAddress1.hashCode() : 0);
        hash = 37 * hash + (this.streetAddress2 != null ? this.streetAddress2.hashCode() : 0);
        hash = 37 * hash + (this.city != null ? this.city.hashCode() : 0);
        hash = 37 * hash + (this.state != null ? this.state.hashCode() : 0);
        hash = 37 * hash + (this.postalCode != null ? this.postalCode.hashCode() : 0);
        hash = 37 * hash + (this.country != null ? this.country.hashCode() : 0);
        return hash;
    }



}
