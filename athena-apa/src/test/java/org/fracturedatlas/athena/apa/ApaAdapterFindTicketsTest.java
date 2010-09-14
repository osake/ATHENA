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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.fracturedatlas.athena.apa.model.BooleanTicketProp;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
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
        for (Ticket t : ticketsToDelete) {
            try {
                apa.deleteTicket(t);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        for (PropField pf : propFieldsToDelete) {
            try {
                apa.deletePropField(pf);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    @Test
    public void testFindTicketsEmptyValue() {
        HashMap<String,String> searchParams = new HashMap<String, String>();
        searchParams.put("Seat Number", "");
        Set<Ticket> tickets = apa.findTickets(searchParams);
        System.out.println(tickets);
        assertEquals(0, tickets.size());
    }

   @Test
   public void testFindTicketsUnknownPropName() {
       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("UNKNOWN_PROP_NAME15", "ABCDEFG");
       Set<Ticket> tickets = apa.findTickets(searchParams);
       System.out.println(tickets);
       assertEquals(0, tickets.size());
   }

   @Test
   public void testFindTicketsInvalidValue() {
       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("Seat Number", "ABCDEFG");
       Set<Ticket> tickets = apa.findTickets(searchParams);
       System.out.println(tickets);
       assertEquals(0, tickets.size());
   }

   @Test
   public void testFindTicketsOneBooleanProperty() {
      Ticket t = new Ticket();

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

      HashMap<String,String> searchParams = new HashMap<String, String>();
      searchParams.put("BOOLEAN_PROP", "true");
      Set<Ticket> tickets = apa.findTickets(searchParams);
      System.out.println(tickets);
      assertEquals(1, tickets.size());
   }

   @Test
   public void testFindTicketsOneIntegerProperty() {
      Ticket t = new Ticket();

      PropField field = new PropField();
      field.setValueType(ValueType.INTEGER);
      field.setName("INTEGER PROP");
      field.setStrict(Boolean.FALSE);
      PropField pf = apa.savePropField(field);

      IntegerTicketProp prop = new IntegerTicketProp();
      prop.setPropField(pf);
      prop.setValue(2);
      t.addTicketProp(prop);
      t = apa.saveTicket(t);

      ticketsToDelete.add(t);
      propFieldsToDelete.add(pf);

       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("INTEGER PROP", "2");
       Set<Ticket> tickets = apa.findTickets(searchParams);
       System.out.println(tickets);
       assertEquals(1, tickets.size());
   }

   @Test
   public void testFindTicketsTwoProperties() {

      Ticket t = new Ticket();

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

      t.addTicketProp(new IntegerTicketProp(pf, 3));
      t.addTicketProp(new BooleanTicketProp(pf2, Boolean.TRUE));
      t = apa.saveTicket(t);

      ticketsToDelete.add(t);
      propFieldsToDelete.add(pf);
      propFieldsToDelete.add(pf2);


       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("Seat Number", "3");
       searchParams.put("locked", "true");
       Set<Ticket> tickets = apa.findTickets(searchParams);
       System.out.println(tickets);
       assertEquals(1, tickets.size());
   }


   @Test
   public void testFindTicketsMultipleProperties() throws ParseException {

      Ticket t = new Ticket();

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
      t.addTicketProp(new DateTimeTicketProp(pf4, DateUtil.parseDate("2010-10-09 16:00:00")));
      t = apa.saveTicket(t);

      ticketsToDelete.add(t);
      propFieldsToDelete.add(pf);
      propFieldsToDelete.add(pf2);
      propFieldsToDelete.add(pf3);
      propFieldsToDelete.add(pf4);


       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("Seat Number", "4");
       searchParams.put("locked", "false");
       searchParams.put("Artist", "ACDC");
       searchParams.put("Date", "2010-10-09 16:00:00");
       Set<Ticket> tickets = apa.findTickets(searchParams);
       System.out.println(tickets);
       assertEquals(1, tickets.size());
   }

   //Testing with two good properties and one bad one
   @Test
   public void testFindTicketsMultipleProperties2() throws ParseException {

      Ticket t = new Ticket();

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
      t.addTicketProp(new DateTimeTicketProp(pf4, DateUtil.parseDate("2010-10-09 16:00:00")));
      t = apa.saveTicket(t);

      ticketsToDelete.add(t);
      propFieldsToDelete.add(pf);
      propFieldsToDelete.add(pf2);
      propFieldsToDelete.add(pf3);
      propFieldsToDelete.add(pf4);

       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("Seat Number", "3");
       searchParams.put("locked", "true");
       searchParams.put("Artist", "Foo");
       Set<Ticket> tickets = apa.findTickets(searchParams);
       System.out.println(tickets);
       assertEquals(0, tickets.size());
   }

   @Test
   public void testFindMultipleTickets() {

      Ticket t = new Ticket();
      Ticket t2 = new Ticket();
      Ticket t3 = new Ticket();
      Ticket t4 = new Ticket();

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

      ticketsToDelete.add(t);
      ticketsToDelete.add(t2);
      ticketsToDelete.add(t3);
      ticketsToDelete.add(t4);
      propFieldsToDelete.add(pf3);


       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("Artist", "ACDC");
       Set<Ticket> tickets = apa.findTickets(searchParams);
       System.out.println(tickets);
       assertEquals(4, tickets.size());
   }

   @Test
   public void testFindTicketsWithoutTime() throws ParseException {


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

      t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
      t = apa.saveTicket(t);

      t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
      t2 = apa.saveTicket(t2);

      t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
      t3 = apa.saveTicket(t3);

      t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
      t4 = apa.saveTicket(t4);


      ticketsToDelete.add(t);
      ticketsToDelete.add(t2);
      ticketsToDelete.add(t3);
      ticketsToDelete.add(t4);
      propFieldsToDelete.add(pf3);

       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("Date", "2010-10-09");
       Set<Ticket> tickets = apa.findTickets(searchParams);
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

      t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
      t = apa.saveTicket(t);

      t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
      t2 = apa.saveTicket(t2);

      t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
      t3 = apa.saveTicket(t3);

      t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
      t4 = apa.saveTicket(t4);


      ticketsToDelete.add(t);
      ticketsToDelete.add(t2);
      ticketsToDelete.add(t3);
      ticketsToDelete.add(t4);
      propFieldsToDelete.add(pf3);

       HashMap<String,String> searchParams = new HashMap<String, String>();
       searchParams.put("Date", "16:00:00");
       Set<Ticket> tickets = apa.findTickets(searchParams);
       assertEquals(0, tickets.size());
   }


}
