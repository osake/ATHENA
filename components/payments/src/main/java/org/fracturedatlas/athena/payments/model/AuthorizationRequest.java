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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class AuthorizationRequest extends Request {

    Boolean storeCard;

    public AuthorizationRequest() {
        setStoreCard(Boolean.FALSE);
    }

    public Boolean getStoreCard() {
        return storeCard;
    }

    public void setStoreCard(Boolean storeCard) {
        this.storeCard = storeCard;
    }

    public Boolean isUsingStoredToken() {
        return this.getCreditCard() != null && this.getCreditCard().getToken() != null;
    }

    @Override
    public String toEscapedString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);

        return builder.append(storeCard).append(super.toEscapedString()).toString();
    }
}
