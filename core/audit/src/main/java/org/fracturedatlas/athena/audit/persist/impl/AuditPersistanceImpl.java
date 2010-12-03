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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.fracturedatlas.athena.audit.model.AuditMessage;
import org.fracturedatlas.athena.audit.persist.AuditPersistance;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author fintan
 */
public class AuditPersistanceImpl implements AuditPersistance {

    @Autowired
    private EntityManagerFactory emf;
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    final String LIMIT = "_limit";
    final String START = "_start";

    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
        System.out.println("emf is " + emf);
    }
    
    @Override
    public AuditMessage saveAuditMessage(AuditMessage am) throws Exception  {
        EntityManager em = this.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            am.setId(LongUserType.massageToLong(am.getId()));
            am = (AuditMessage) em.merge(am);
            em.getTransaction().commit();
            return am;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public List getAuditMessages(AthenaSearch athenaSearch) {
        logger.debug("Searching for AuditMessages matching [{}]", athenaSearch);
        EntityManager em = this.emf.createEntityManager();
        Set<String> value = null;
        Query query = null;
        AuditMessage tempAuditMessage = null;
        int limit = -1;
        int start = -1;
        try {
            String limitString = athenaSearch.getSearchModifier(LIMIT);
            if (limitString != null) {
                limit = Integer.parseInt(limitString);
            }
        } catch (NumberFormatException ex) {
            logger.error("Error While searching [{}]", athenaSearch.asList());
            logger.error(ex.getMessage(), ex);
            limit = -1;
        }
        if (limit == 0) {
            return new ArrayList<AuditMessage>();
        }
        try {
            String startString = athenaSearch.getSearchModifier(START);
            if (startString != null) {
                start = Integer.parseInt(startString);
            }
        } catch (NumberFormatException ex) {
            logger.error("Error While searching [{}]", athenaSearch.asList());
            logger.error(ex.getMessage(), ex);
            start = -1;
        }
        List<AuditMessage> finishedAuditMessages = null;
        String queryString = null;
        Operator operator = null;
        String name = null;
        Iterator<String> it = null;
        String singleValue = null;
        Iterator<AthenaSearchConstraint> searchListItr = athenaSearch.asList().iterator();
        queryString = "FROM AuditMessage am WHERE ";
        int i = 1;
        HashMap<String, String> valuesTable = new HashMap<String, String>();
        AthenaSearchConstraint apc = null;
        try {
            while (searchListItr.hasNext()) {
                apc = searchListItr.next();
                name = apc.getParameter();
                operator = apc.getOper();
                value = apc.getValueSet();
                if (value.size() > 1) {
                    it = value.iterator();
                    while (it.hasNext()) {
                        singleValue = it.next();
                        //check if name is valid
                        queryString = queryString + "am." + name + operator.getOperatorString() + i;
                        valuesTable.put("value" + i, singleValue);
                        i++;
                        if (it.hasNext()) {
                            queryString = queryString + " AND ";
                        }
                    }
                } else {
                    queryString = queryString + "am." + name + operator.getOperatorString() + i;
                    valuesTable.put("value" + i, value.iterator().next());
                }
                if (searchListItr.hasNext()) {
                    queryString = queryString + " AND ";
                }

            }
            query = em.createQuery(queryString);
            for (int j = 1; j <= i; j++) {
                query.setParameter("value" + i, valuesTable.get("value" + i));
            }
            finishedAuditMessages = query.getResultList();
            int startCounter = 0;
            int limitCounter = 0;
            if (start > 0) {
                if (start > finishedAuditMessages.size()) {
                    return new ArrayList<AuditMessage>();
                }
            }
            if (limit > 0) {
                if (limit < finishedAuditMessages.size()) {
                    finishedAuditMessages = finishedAuditMessages.subList(0, limit);
                }
            }
            return finishedAuditMessages;
        } catch (Exception ex) {
            logger.error("Error While searching [{}]", athenaSearch.asList());
            logger.error(ex.getMessage(), ex);
            return new ArrayList<AuditMessage>();
        } finally {
            cleanup(em);
        }
    }

    private void cleanup(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }
}
