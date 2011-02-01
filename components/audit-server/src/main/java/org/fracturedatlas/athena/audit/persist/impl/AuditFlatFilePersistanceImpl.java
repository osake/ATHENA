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
package org.fracturedatlas.athena.audit.persist.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.fracturedatlas.athena.audit.model.AuditMessage;
import org.fracturedatlas.athena.audit.persist.AuditPersistance;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;


public class AuditFlatFilePersistanceImpl implements AuditPersistance {

    protected Logger logger = LoggerFactory.getLogger("AuditFile");
    private static Properties props;

    static {
        props = new Properties();
        ClassPathResource cpr = new ClassPathResource("athena-audit.properties");
        try {
            InputStream in = cpr.getInputStream();
            props.load(in);
            in.close();

        } catch (Exception e) {
            Logger log2 = LoggerFactory.getLogger(AuditFlatFilePersistanceImpl.class);
            log2.error(e.getMessage(), e);
        }
    }

    @Override
    public AuditMessage saveAuditMessage(AuditMessage am) throws Exception {
        Long id = am.getDateTime();
        am.setId(id);
        logger.info(am.toString());
        return am;
    }

    @Override
    public List<AuditMessage> getAuditMessages(AthenaSearch as) {
        return new ArrayList<AuditMessage>();
    }


}
