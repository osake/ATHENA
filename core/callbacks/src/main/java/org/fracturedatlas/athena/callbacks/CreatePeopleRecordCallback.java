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
package org.fracturedatlas.athena.callbacks;

import java.util.Collection;
import java.util.List;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.client.RecordUtil;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class CreatePeopleRecordCallback extends AbstractAthenaCallback {
    
    @Autowired
    AthenaComponent athenaPeople;
    
    @Override
    public PTicket afterSave(String type, PTicket record) {
        if(RecordUtil.hasPersonInformation(record)) {
            
            createPersonRecord(record);
        }
        return removePersonRecordInformation(record);
    }
    
    public void createPersonRecord(PTicket record) {
        
        PTicket existingPerson = findPerson(record.get("email"));
        
        if(existingPerson == null) {    
            PTicket newPerson = new PTicket();
            newPerson.put("firstName", record.get("firstName"));
            newPerson.put("lastName", record.get("lastName"));
            newPerson.put("email", record.get("email"));
            newPerson.put("organizationId", record.get("organizationId"));
            existingPerson = athenaPeople.save("person", newPerson);
        }
        record.put("personId", existingPerson.getIdAsString());
    }
        
    public PTicket findPerson(String email) {
        AthenaSearch search = new AthenaSearch.Builder()
                                  .type("person")
                                  .and("email", Operator.EQUALS, email)
                                  .build();
        Collection<PTicket> people = athenaPeople.find("person", search);
        if(people.size() == 0) {
            return null;
        } else {
            return people.iterator().next();
        }
    }
    
    public PTicket removePersonRecordInformation(PTicket record) {
        record.getProps().remove("firstName");
        record.getProps().remove("lastName");
        record.getProps().remove("email");
        return record;
    }

    public AthenaComponent getAthenaPeople() {
        return athenaPeople;
    }

    public void setAthenaPeople(AthenaComponent athenaPeople) {
        this.athenaPeople = athenaPeople;
    }
}
