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

package org.fracturedatlas.athena.helper.bulk.manager;

import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.web.manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BulkManager {

    @Autowired
    RecordManager recordManager;

    @Autowired
    PropFieldManager fieldManager;

    @Autowired
    ApaAdapter apa;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

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

    public PropFieldManager getFieldManager() {
        return fieldManager;
    }

    public void setFieldManager(PropFieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }
}
