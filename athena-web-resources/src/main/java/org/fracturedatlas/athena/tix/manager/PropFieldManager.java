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
package org.fracturedatlas.athena.tix.manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.tix.exception.ForbiddenException;
import org.fracturedatlas.athena.tix.exception.InvalidFieldNameException;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.util.AllowedCharacterCheck;
import org.springframework.beans.factory.annotation.Autowired;

public class PropFieldManager {

    @Autowired
    ApaAdapter apa;

    public PropField[] findPropFields() {
      return apa.getPropFields().toArray(new PropField[0]);
    }

    public PropField getPropField(Object id) {
        return apa.getPropField(id);
    }
    
    public PropField savePropField(PropField pf) throws Exception {
        if(!AllowedCharacterCheck.confirm(pf.getName())) {
                //TODO: if AllowedCharacterCheck throws this exception, we can be more specific with this message
        	throw new InvalidFieldNameException("Field name [" +
        				pf.getName() + "] is invalid.  Either it contains invalid characters, no characters, or it is too long.");
       }

        return apa.savePropField(pf);
    }

    public PropValue getPropValue(Object propValueId) {
            return apa.getPropValue(propValueId);
    }

    public PropValue[] getPropValueList(Object propFieldId) {
            return apa.getPropValues(propFieldId).toArray(new PropValue[0]);
    }

    public PropValue savePropValue(PropValue propValue) {
        return apa.savePropValue(propValue);
    }

    /**
     * Add a propValue to the propField identified by propFieldId
     * @param propFieldId the id of the propField ot attach this value to
     * @param propValue the new propValue to save
     * @return the new/updated propValue
     */
    public PropValue savePropValue(Object propFieldId, PropValue propValue) {
        PropField propField = apa.getPropField(propFieldId);
        propValue.setPropField(propField);
        return apa.savePropValue(propValue);
    }
    
	public boolean deletePropField(Object id) {
		return apa.deletePropField(id);		
	}
	
	public boolean deletePropValue(Object id) {
		return apa.deletePropValue(id);		
	}
}

