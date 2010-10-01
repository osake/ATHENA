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
package org.fracturedatlas.athena.web.manager;

import com.sun.jersey.api.NotFoundException;
import java.util.Collection;

import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.web.exception.InvalidFieldNameException;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.util.AllowedCharacterCheck;
import org.fracturedatlas.athena.web.exception.AthenaException;
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
        if (!AllowedCharacterCheck.confirm(pf.getName())) {
            //TODO: if AllowedCharacterCheck throws this exception, we can be more specific with this message
            throw new InvalidFieldNameException("Field name ["
                    + pf.getName() + "] is invalid.  Either it contains invalid characters, no characters, or it is too long.");
        }

        return apa.savePropField(pf);
    }

    public PropField updatePropField(PropField pf, Object idToUpdate) throws Exception {
        
        if(pf.getId() == null) {
            throw new AthenaException("Cannot");
        } 
        PropField existingPropField = apa.getPropField(idToUpdate);
        if(existingPropField == null) {
            throw new NotFoundException();
        } else if (!IdAdapter.isEqual(pf.getId(), idToUpdate)) {
            throw new AthenaException("Requested update to [" + idToUpdate + "] but sent field with id [" + pf.getId() + "]");
        }      

        return savePropField(pf);
    }

    public PropValue getPropValue(Object propFieldId, Object propValueId) {
        PropValue propValue = null;

        Collection<PropValue> propValues = apa.getPropValues(propFieldId);
        System.out.println("HEY: " + propValues);
        if (propValues != null) {
            for (PropValue v : propValues) {
                if (IdAdapter.isEqual(propValueId, v.getId())) {
                    propValue = v;
                    break;
                }
            }
        }

        return propValue;
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

    public Boolean deletePropField(Object id) {
        PropField propField = getPropField(id);
        if(propField == null) {
            return false;
        } else {
            return apa.deletePropField(id);
        }
    }

    public Boolean deletePropValue(Object propFieldId, Object propValueId) {
        PropField propField = getPropField(propFieldId);
        if(propField == null) {
            return false;
        } else {
            apa.deletePropValue(propFieldId, propValueId);
            return true;
        }
    }
}

