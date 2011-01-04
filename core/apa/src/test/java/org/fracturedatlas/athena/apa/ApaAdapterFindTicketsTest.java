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
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.search.AthenaSearch;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import org.fracturedatlas.athena.apa.model.BooleanTicketProp;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.*;
import static org.junit.Assert.*;

public class ApaAdapterFindTicketsTest extends BaseApaAdapterTest {

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
        AthenaSearch as = new AthenaSearch();
        as.addConstraint("SEAT NUMBER", Operator.EQUALS, "");
        as.setType("ticket");

        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }

    @Test
    public void testFindTicketsUnknownPropName() {
        AthenaSearch as = new AthenaSearch();
        as.addConstraint("UNKNOWN_PROP_NAME15", Operator.EQUALS, "ABCDEFG");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }

    @Test
    public void testFindTicketsInvalidValue() {
        AthenaSearch as = new AthenaSearch();
        as.addConstraint("Seat Number", Operator.EQUALS, "ABCDEFG");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }

    @Test
    public void testFindTicketsOneBooleanProperty() {
        Ticket t = new Ticket();
        t.setType("ticket");

        PropField field = new PropField();
        field.setValueType(ValueType.BOOLEAN);
        field.setName("BOOLEAN_PROP");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);

        BooleanTicketProp prop = new BooleanTicketProp();
        prop.setPropField(pf);
        prop.setValue(Boolean.TRUE);
        t.addTicketProp(prop);
        t = apa.saveTicket(t);

        ticketsToDelete.add(t);
        propFieldsToDelete.add(pf);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("BOOLEAN_PROP", Operator.EQUALS, "true");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());
    }

    @Test
    public void testFindTicketsOneIntegerProperty() {
        Ticket t = new Ticket();
        t.setType("ticket");

        PropField field = new PropField();
        field.setValueType(ValueType.INTEGER);
        field.setName("INTEGER_PROP");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);

        IntegerTicketProp prop = new IntegerTicketProp();
        prop.setPropField(pf);
        prop.setValue(2);
        t.addTicketProp(prop);
        t = apa.saveTicket(t);

        ticketsToDelete.add(t);
        propFieldsToDelete.add(pf);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("INTEGER_PROP", Operator.EQUALS, "2");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());

        for (Ticket ticket : tickets) {
            PTicket pTicket = t.toClientTicket();
            for (Entry<String, String> entry : pTicket.getProps().entrySet()) {
                assertEquals(entry.getKey(), "INTEGER_PROP");
                assertEquals(entry.getValue(), "2");
            }
        }
    }

    @Test
    public void testFindTicketsTwoProperties() {

        Ticket t = new Ticket();
        t.setType("ticket");

        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "SEAT_NUMBER", StrictType.NOT_STRICT));
        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "LOCKED", StrictType.NOT_STRICT));

        t.addTicketProp(new IntegerTicketProp(pf, 3));
        t.addTicketProp(new BooleanTicketProp(pf2, Boolean.TRUE));
        t = apa.saveTicket(t);

        ticketsToDelete.add(t);
        propFieldsToDelete.add(pf);
        propFieldsToDelete.add(pf2);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("SEAT_NUMBER", Operator.EQUALS, "3");
        as.addConstraint("LOCKED", Operator.EQUALS, "true");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());
    }

    //@Test
    public void testFindTicketsMultipleProperties() throws ParseException {

        Ticket t = new Ticket();
        t.setType("ticket");

        PropField field = new PropField();
        field.setValueType(ValueType.INTEGER);
        field.setName("Seat Number");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        field = new PropField();
        field.setValueType(ValueType.BOOLEAN);
        field.setName("locked");
        field.setStrict(Boolean.FALSE);
        PropField pf2 = apa.savePropField(field);
        field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("Artist");
        field.setStrict(Boolean.FALSE);
        PropField pf3 = apa.savePropField(field);
        field = new PropField();
        field.setValueType(ValueType.DATETIME);
        field.setName("Date");
        field.setStrict(Boolean.FALSE);
        PropField pf4 = apa.savePropField(field);

        t.addTicketProp(new IntegerTicketProp(pf, 4));
        t.addTicketProp(new BooleanTicketProp(pf2, Boolean.FALSE));
        t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t.addTicketProp(new DateTimeTicketProp(pf4, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t = apa.saveTicket(t);

        ticketsToDelete.add(t);
        propFieldsToDelete.add(pf);
        propFieldsToDelete.add(pf2);
        propFieldsToDelete.add(pf3);
        propFieldsToDelete.add(pf4);


        AthenaSearch as = new AthenaSearch();
        as.addConstraint("Seat Number", Operator.EQUALS, "4");
        as.addConstraint("locked", Operator.EQUALS, "false");
        as.addConstraint("Artist", Operator.EQUALS, "ACDC");
        as.addConstraint("Date", Operator.EQUALS, "\'2010-10-14T13:33:50-04:00\'");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(1, tickets.size());
    }

    //Testing with two good properties and one bad one
    @Test
    public void testFindTicketsMultipleProperties2() throws ParseException {

        Ticket t = new Ticket();
        t.setType("ticket");

        PropField field = new PropField();
        field.setValueType(ValueType.INTEGER);
        field.setName("Seat Number");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        field = new PropField();
        field.setValueType(ValueType.BOOLEAN);
        field.setName("locked");
        field.setStrict(Boolean.FALSE);
        PropField pf2 = apa.savePropField(field);
        field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("Artist");
        field.setStrict(Boolean.FALSE);
        PropField pf3 = apa.savePropField(field);
        field = new PropField();
        field.setValueType(ValueType.DATETIME);
        field.setName("Date");
        field.setStrict(Boolean.FALSE);
        PropField pf4 = apa.savePropField(field);

        t.addTicketProp(new IntegerTicketProp(pf, 3));
        t.addTicketProp(new BooleanTicketProp(pf2, Boolean.FALSE));
        t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t.addTicketProp(new DateTimeTicketProp(pf4, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t = apa.saveTicket(t);

        ticketsToDelete.add(t);
        propFieldsToDelete.add(pf);
        propFieldsToDelete.add(pf2);
        propFieldsToDelete.add(pf3);
        propFieldsToDelete.add(pf4);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("Seat Number", Operator.EQUALS, "3");
        as.addConstraint("locked", Operator.EQUALS, "true");
        as.addConstraint("Artist", Operator.EQUALS, "Foo");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);

        assertEquals(0, tickets.size());
    }


    /*
     * Six tickets in the DB.  Looking for four
     */
    @Test
    public void testFindMultipleTickets2() {

        List<Ticket> winners = new ArrayList<Ticket>();

        Ticket t = new Ticket("ticket");
        Ticket t2 = new Ticket("ticket");
        Ticket t3 = new Ticket("ticket");
        Ticket t4 = new Ticket("ticket");
        Ticket t5 = new Ticket("ticket");
        Ticket t6 = new Ticket("ticket");

        PropField field = new PropField(ValueType.STRING, "Artist", Boolean.FALSE);
        PropField pf3 = apa.savePropField(field);

        t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t = apa.saveTicket(t);

        t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t2 = apa.saveTicket(t2);

        t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t3 = apa.saveTicket(t3);

        t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t4 = apa.saveTicket(t4);

        t5.addTicketProp(new StringTicketProp(pf3, "Warrant"));
        t5 = apa.saveTicket(t5);

        t6.addTicketProp(new StringTicketProp(pf3, "Warrant"));
        t6 = apa.saveTicket(t6);

        ticketsToDelete.add(t);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        ticketsToDelete.add(t5);
        ticketsToDelete.add(t6);
        propFieldsToDelete.add(pf3);

        winners.add(t);
        winners.add(t2);
        winners.add(t3);
        winners.add(t4);


        AthenaSearch as = new AthenaSearch.Builder().type("ticket")
                                                    .and(new AthenaSearchConstraint("Artist", Operator.EQUALS, "ACDC"))
                                                    .build();
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(4, tickets.size());

        doCollectionsContainSameElements(winners, tickets);
    }

    /*
     * Six tickets in the DB each with two properties.  Looking for three
     */
    @Test
    public void testFindMultipleTickets3() {

        List<Ticket> winners = new ArrayList<Ticket>();

        Ticket t = new Ticket();
        Ticket t2 = new Ticket();
        Ticket t3 = new Ticket();
        Ticket t4 = new Ticket();
        Ticket t5 = new Ticket();
        Ticket t6 = new Ticket();

        PropField pf3 = apa.savePropField(new PropField(ValueType.STRING, "Artist", Boolean.FALSE));
        PropField pf4 = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));

        t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t.addTicketProp(new IntegerTicketProp(pf4, 50));
        t.setType("ticket");
        t = apa.saveTicket(t);

        t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t2.addTicketProp(new IntegerTicketProp(pf4, 50));
        t2.setType("ticket");
        t2 = apa.saveTicket(t2);

        t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t3.addTicketProp(new IntegerTicketProp(pf4, 100));
        t3.setType("ticket");
        t3 = apa.saveTicket(t3);

        t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t4.addTicketProp(new IntegerTicketProp(pf4, 50));
        t4.setType("ticket");
        t4 = apa.saveTicket(t4);

        t5.addTicketProp(new StringTicketProp(pf3, "Warrant"));
        t5.addTicketProp(new IntegerTicketProp(pf4, 50));
        t5.setType("ticket");
        t5 = apa.saveTicket(t5);

        t6.addTicketProp(new StringTicketProp(pf3, "Warrant"));
        t6.addTicketProp(new IntegerTicketProp(pf4, 75));
        t6.setType("ticket");
        t6 = apa.saveTicket(t6);

        ticketsToDelete.add(t);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        ticketsToDelete.add(t5);
        ticketsToDelete.add(t6);
        propFieldsToDelete.add(pf3);
        propFieldsToDelete.add(pf4);

        winners.add(t);
        winners.add(t2);
        winners.add(t4);

        AthenaSearch as = new AthenaSearch.Builder().type("ticket")
                                                    .and(new AthenaSearchConstraint("Artist", Operator.EQUALS, "ACDC"))
                                                    .and(new AthenaSearchConstraint("PRICE", Operator.EQUALS, "50"))
                                                    .build();
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(3, tickets.size());

        doCollectionsContainSameElements(winners, tickets);
    }

    /*
     * Six tickets in the DB each with two properties.  Looking for zero
     */
    @Test
    public void testFindMultipleTickets4() {

        List<Ticket> winners = new ArrayList<Ticket>();
        
        Ticket t = new Ticket();
        Ticket t2 = new Ticket();
        Ticket t3 = new Ticket();
        Ticket t4 = new Ticket();
        Ticket t5 = new Ticket();
        Ticket t6 = new Ticket();

        PropField pf3 = apa.savePropField(new PropField(ValueType.STRING, "Artist", Boolean.FALSE));
        PropField pf4 = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));

        t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t.addTicketProp(new IntegerTicketProp(pf4, 50));
        t.setType("ticket");
        t = apa.saveTicket(t);

        t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t2.addTicketProp(new IntegerTicketProp(pf4, 50));
        t2.setType("ticket");
        t2 = apa.saveTicket(t2);

        t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t3.addTicketProp(new IntegerTicketProp(pf4, 100));
        t3.setType("ticket");
        t3 = apa.saveTicket(t3);

        t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t4.addTicketProp(new IntegerTicketProp(pf4, 50));
        t4.setType("ticket");
        t4 = apa.saveTicket(t4);

        t5.addTicketProp(new StringTicketProp(pf3, "Warrant"));
        t5.addTicketProp(new IntegerTicketProp(pf4, 50));
        t5.setType("ticket");
        t5 = apa.saveTicket(t5);

        t6.addTicketProp(new StringTicketProp(pf3, "Warrant"));
        t6.addTicketProp(new IntegerTicketProp(pf4, 75));
        t6.setType("ticket");
        t6 = apa.saveTicket(t6);

        ticketsToDelete.add(t);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        ticketsToDelete.add(t5);
        ticketsToDelete.add(t6);
        propFieldsToDelete.add(pf3);
        propFieldsToDelete.add(pf4);



        AthenaSearch as = new AthenaSearch.Builder().type("ticket")
                                                    .and(new AthenaSearchConstraint("Artist", Operator.EQUALS, "Warrant"))
                                                    .and(new AthenaSearchConstraint("PRICE", Operator.EQUALS, "100"))
                                                    .build();
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
        doCollectionsContainSameElements(winners, tickets);

    }

    /*
     * Six tickets in the DB each with two properties.  Looking for four
     */
    @Test
    public void testFindMultipleTickets5() {

        List<Ticket> winners = new ArrayList<Ticket>();

        Ticket t = new Ticket("ticket");
        Ticket t2 = new Ticket("ticket");
        Ticket t3 = new Ticket("ticket");
        Ticket t4 = new Ticket("ticket");
        Ticket t5 = new Ticket("ticket");
        Ticket t6 = new Ticket("ticket");

        PropField pf3 = apa.savePropField(new PropField(ValueType.STRING, "Artist", Boolean.FALSE));
        PropField pf4 = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));

        t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t.addTicketProp(new IntegerTicketProp(pf4, 50));
        t = apa.saveTicket(t);

        t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t2.addTicketProp(new IntegerTicketProp(pf4, 50));
        t2 = apa.saveTicket(t2);

        t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t3.addTicketProp(new IntegerTicketProp(pf4, 100));
        t3 = apa.saveTicket(t3);

        t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
        t4.addTicketProp(new IntegerTicketProp(pf4, 50));
        t4 = apa.saveTicket(t4);

        t5.addTicketProp(new StringTicketProp(pf3, "Warrant"));
        t5.addTicketProp(new IntegerTicketProp(pf4, 50));
        t5 = apa.saveTicket(t5);

        t6.addTicketProp(new StringTicketProp(pf3, "Warrant"));
        t6.addTicketProp(new IntegerTicketProp(pf4, 75));
        t6 = apa.saveTicket(t6);

        ticketsToDelete.add(t);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        ticketsToDelete.add(t5);
        ticketsToDelete.add(t6);
        propFieldsToDelete.add(pf3);
        propFieldsToDelete.add(pf4);
        
        winners.add(t);
        winners.add(t2);
        winners.add(t4);
        winners.add(t5);


        AthenaSearch as = new AthenaSearch.Builder().type("ticket")
                                                    .and(new AthenaSearchConstraint("PRICE", Operator.EQUALS, "50"))
                                                    .build();
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(4, tickets.size());
        doCollectionsContainSameElements(winners, tickets);
    }

    @Test
    public void testFindTicketsWithoutTime() throws ParseException {

        Ticket t = new Ticket("ticket");
        Ticket t2 = new Ticket("ticket");
        Ticket t3 = new Ticket("ticket");
        Ticket t4 = new Ticket("ticket");

        PropField field = new PropField();
        field = new PropField();
        field.setValueType(ValueType.DATETIME);
        field.setName("Date");
        field.setStrict(Boolean.FALSE);
        PropField pf3 = apa.savePropField(field);

        t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t = apa.saveTicket(t);

        t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t2 = apa.saveTicket(t2);

        t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t3 = apa.saveTicket(t3);

        t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t4 = apa.saveTicket(t4);


        ticketsToDelete.add(t);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        propFieldsToDelete.add(pf3);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("Date", Operator.EQUALS, "2010-10-09");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }

    @Test
    public void testFindTicketsWithoutDate() throws ParseException {


        Ticket t = new Ticket();
        Ticket t2 = new Ticket();
        Ticket t3 = new Ticket();
        Ticket t4 = new Ticket();

        PropField field = new PropField();
        field = new PropField();
        field.setValueType(ValueType.DATETIME);
        field.setName("Date");
        field.setStrict(Boolean.FALSE);
        PropField pf3 = apa.savePropField(field);

        t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t = apa.saveTicket(t);

        t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t2 = apa.saveTicket(t2);

        t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t3 = apa.saveTicket(t3);

        t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t4 = apa.saveTicket(t4);


        ticketsToDelete.add(t);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        propFieldsToDelete.add(pf3);

        AthenaSearch as = new AthenaSearch();
        as.addConstraint("Date", Operator.EQUALS, "16:00:00");
        as.setType("ticket");
        Collection<Ticket> tickets = apa.findTickets(as);
        assertEquals(0, tickets.size());
    }
}
