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
package org.fracturedatlas.athena.apa;

import java.text.ParseException;
import java.util.ArrayList;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.search.AthenaSearch;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PField;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

public class ApaAdapterFindTicketsTest extends BaseApaAdapterTest {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    public ApaAdapterFindTicketsTest() {
        super();
    }

    @Before
    public void setupTickets() {
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testFindTicketsEmptyValue() {
        addPropField(ValueType.STRING,"SEAT NUMBER",Boolean.FALSE);
        AthenaSearch as = new AthenaSearch();
        as.addConstraint("SEAT NUMBER", Operator.EQUALS, "");
        as.setType("ticket");

        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }

    @Test
    public void testFindTicketsUnknownPropName() {
        AthenaSearch as = new AthenaSearch();
        as.addConstraint("UNKNOWN_PROP_NAME15", Operator.EQUALS, "ABCDEFG");
        as.setType("ticket");
        try{
            Collection<PTicket> tickets = apa.findTickets(as);
            fail("Needed ApaException");
        } catch (ApaException ae) {
            //pass
        }
    }

    @Test
    public void testFindTicketsInvalidValue() {
        addPropField(ValueType.STRING,"Seat Number",Boolean.FALSE);
        AthenaSearch as = new AthenaSearch();
        as.addConstraint("Seat Number", Operator.EQUALS, "ABCDEFG");
        as.setType("ticket");
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }

    @Test
    public void testFindTicketsOneBooleanProperty() {

        PField pf = addPropField(ValueType.BOOLEAN,"BOOLEAN_PROP",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("BOOLEAN_PROP", "true");
        t = apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("BOOLEAN_PROP", Operator.EQUALS, "true");
        as.setType("ticket");
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());
    }

    @Test
    public void testFindTicketsOneDatetimeProperty() {

        PField pf = addPropField(ValueType.DATETIME,"WHENISIT",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("WHENISIT", "2009-08-08T04:05:06Z");
        t = apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("WHENISIT", Operator.EQUALS, "2009-08-08T04:05:06Z");
        as.setType("ticket");
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());
    }

    @Test
    public void testFindTicketsOneIntegerProperty() {

        PField pf = addPropField(ValueType.INTEGER,"INTEGER_PROP",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("INTEGER_PROP", "2");
        t = apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("INTEGER_PROP", Operator.EQUALS, "2");
        as.setType("ticket");
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());

        for (PTicket ticket : tickets) {
            for (String key : t.getProps().keySet()) {
                assertEquals(key, "INTEGER_PROP");
                assertEquals(t.get(key), "2");
            }
        }
    }

    @Test
    public void testFindTicketsTwoProperties() {

        addPropField(ValueType.INTEGER,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"LOCKED",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("SEAT_NUMBER", "3");
        t.put("LOCKED", "true");
        t = apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch.Builder()
                                          .type("ticket")
                                          .and("SEAT_NUMBER", Operator.EQUALS, "3")
                                          .and("LOCKED", Operator.EQUALS, "true")
                                          .build();

        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());
    }

    //@Test
    public void testFindTicketsMultipleProperties() throws ParseException {

        addPropField(ValueType.INTEGER,"Seat Number",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"locked",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"artist",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"date",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("Seat Number", "4");
        t.put("locked", "false");
        t.put("artist", "ACDC");
        t.put("date", "2010-10-14T13:33:50-04:00");
        t = apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch.Builder()
                                          .type("ticket")
                                          .and("Seat Number", Operator.EQUALS, "4")
                                          .and("locked", Operator.EQUALS, "false")
                                          .and("Artist", Operator.EQUALS, "ACDC")
                                          .and("Date", Operator.EQUALS, "\'2010-10-14T13:33:50-04:00\'")
                                          .build();

        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());
    }

    //This tests that any boolean prop != "true" (ignoring case is interpreted as "false"
    //See: BooleanTicketProp.setValue() and Boolean.parseBoolean()
    @Test
    public void testFindTicketsIncorrectBooleanProperties() throws ParseException {

        addPropField(ValueType.INTEGER,"Seat Number",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"locked",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"artist",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"date",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("Seat Number", "4");
        t.put("artist", "ACDC");
        t.put("date", "2010-10-14T13:33:50-04:00");
        t.put("locked", "false");
        logger.debug(t.toString());
        t = apa.saveRecord(t);
        logger.debug(t.toString());

        AthenaSearch as = new AthenaSearch.Builder()
                                          .type("ticket")
                                          .and("Seat Number", Operator.EQUALS, "4")
                                          .and("locked", Operator.EQUALS, "false")
                                          .and("artist", Operator.EQUALS, "foo")
                                          .build();
        Collection<PTicket> tickets = apa.findTickets(as);

        for(PTicket ticket : tickets) {
            logger.debug(ticket.toString());
        }

        assertEquals(1, tickets.size());
    }


    //Testing with two good properties and one bad one
    @Test
    public void testFindTicketsMultipleProperties2() throws ParseException {

        addPropField(ValueType.INTEGER,"Seat Number",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"locked",Boolean.FALSE);
        addPropField(ValueType.STRING,"artist",Boolean.FALSE);
        addPropField(ValueType.STRING,"date",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("Seat Number", "4");
        t.put("artist", "ACDC");
        t.put("date", "2010-10-14T13:33:50-04:00");
        t.put("locked", "false");
        logger.debug(t.toString());
        t = apa.saveRecord(t);
        logger.debug(t.toString());

        AthenaSearch as = new AthenaSearch.Builder()
                                          .type("ticket")
                                          .and("Seat Number", Operator.EQUALS, "4")
                                          .and("locked", Operator.EQUALS, "false")
                                          .and("artist", Operator.EQUALS, "foo")
                                          .build();
        Collection<PTicket> tickets = apa.findTickets(as);

        for(PTicket ticket : tickets) {
            logger.debug(ticket.toString());
        }

        assertEquals(0, tickets.size());
    }


    /*
     * Six tickets in the DB.  Looking for four
     */
    @Test
    public void testFindMultipleTickets2() {

        List<PTicket> winners = new ArrayList<PTicket>();

        addPropField(ValueType.STRING,"Artist",Boolean.FALSE);


        PTicket t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        winners.add(apa.saveRecord(t));
        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        winners.add(apa.saveRecord(t));
        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        winners.add(apa.saveRecord(t));
        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        winners.add(apa.saveRecord(t));
        t = new PTicket("ticket");
        t.put("Artist", "Warant");
        apa.saveRecord(t);
        t = new PTicket("ticket");
        t.put("Artist", "Van Halen");
        apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch.Builder().type("ticket")
                                                    .and(new AthenaSearchConstraint("Artist", Operator.EQUALS, "ACDC"))
                                                    .build();
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(tickets.size(), winners.size());

        doCollectionsContainSameElements(winners, tickets);
    }

    /*
     * Six tickets in the DB each with two properties.  Looking for three
     */
    @Test
    public void testFindMultipleTickets3() {
        List<PTicket> winners = new ArrayList<PTicket>();

        addPropField(ValueType.STRING,"Artist",Boolean.FALSE);
        addPropField(ValueType.INTEGER,"PRICE",Boolean.FALSE);

        PTicket t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "50");
        winners.add(apa.saveRecord(t));

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "100");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "50");
        winners.add(apa.saveRecord(t));

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "100");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "Warrant");
        t.put("PRICE", "50");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "Warrant");
        t.put("PRICE", "75");
        apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch.Builder().type("ticket")
                                                    .and("Artist", Operator.EQUALS, "ACDC")
                                                    .and("PRICE", Operator.EQUALS, "50")
                                                    .build();
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(2, tickets.size());

        doCollectionsContainSameElements(winners, tickets);
    }

    /*
     * Six tickets in the DB each with two properties.  Looking for zero
     */
    @Test
    public void testFindMultipleTickets4() {

        List<PTicket> winners = new ArrayList<PTicket>();

        addPropField(ValueType.STRING,"Artist",Boolean.FALSE);
        addPropField(ValueType.INTEGER,"PRICE",Boolean.FALSE);

        PTicket t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "50");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "100");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "50");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "100");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "Warrant");
        t.put("PRICE", "50");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "Warrant");
        t.put("PRICE", "75");
        apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch.Builder().type("ticket")
                                                    .and("Artist", Operator.EQUALS, "Warrant")
                                                    .and("PRICE", Operator.EQUALS, "503")
                                                    .build();
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());

        doCollectionsContainSameElements(winners, tickets);

    }

    /*
     * Six tickets in the DB each with two properties.  Looking for four
     */
    @Test
    public void testFindMultipleTickets5() {
        List<PTicket> winners = new ArrayList<PTicket>();

        addPropField(ValueType.STRING,"Artist",Boolean.FALSE);
        addPropField(ValueType.INTEGER,"PRICE",Boolean.FALSE);

        PTicket t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "50");
        winners.add(apa.saveRecord(t));

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "100");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "50");
        winners.add(apa.saveRecord(t));

        t = new PTicket("ticket");
        t.put("Artist", "ACDC");
        t.put("PRICE", "100");
        apa.saveRecord(t);

        t = new PTicket("ticket");
        t.put("Artist", "Warrant");
        t.put("PRICE", "50");
        winners.add(apa.saveRecord(t));

        t = new PTicket("ticket");
        t.put("Artist", "Warrant");
        t.put("PRICE", "75");
        apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch.Builder().type("ticket")
                                                    .and("PRICE", Operator.EQUALS, "50")
                                                    .build();
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(3, tickets.size());

        doCollectionsContainSameElements(winners, tickets);
    }

    @Test
    public void testFindTicketsWithoutTime() throws ParseException {
        PField pf = addPropField(ValueType.DATETIME,"WHENISIT",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("WHENISIT", "2009-08-08T04:05:06Z");
        t = apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("WHENISIT", Operator.EQUALS, "2009-08-08");
        as.setType("ticket");
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }

    @Test
    public void testFindTicketsWithoutDate() throws ParseException {
        PField pf = addPropField(ValueType.DATETIME,"WHENISIT",Boolean.FALSE);
        PTicket t = new PTicket("ticket");
        t.put("WHENISIT", "2009-08-08T04:05:06Z");
        t = apa.saveRecord(t);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("WHENISIT", Operator.EQUALS, "04:05:06Z");
        as.setType("ticket");
        Collection<PTicket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }
}
