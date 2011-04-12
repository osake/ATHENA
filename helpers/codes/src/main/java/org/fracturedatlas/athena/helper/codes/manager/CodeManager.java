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

package org.fracturedatlas.athena.helper.codes.manager;

import java.util.Set;
import java.util.TreeSet;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.springframework.beans.factory.annotation.Autowired;


public class CodeManager {

    @Autowired
    RecordManager recordManager;

    @Autowired
    ApaAdapter apa;

    public static final String CODED_TYPE = "ticket";

    public Code getCode(Code code) {
        return new Code();
    }

    public Code createCode(Code code) {
        Set<PTicket> ticketsForThisCode = new TreeSet<PTicket>();

        for(String ticketId : code.getTickets()) {
            PTicket t = recordManager.getTicket(CODED_TYPE, ticketId);
            ticketsForThisCode.add(t);
        }

        for(PTicket t : ticketsForThisCode) {
            recordManager.updateRecord(CODED_TYPE, t);
        }

        return new Code();
    }

    public ApaAdapter getApa() {
        return apa;
    }

    public void setApa(ApaAdapter apa) {
        this.apa = apa;
    }

    public RecordManager getRecordManager() {
        return recordManager;
    }

    public void setRecordManager(RecordManager recordManager) {
        this.recordManager = recordManager;
    }

    

}
