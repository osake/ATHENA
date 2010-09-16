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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.fracturedatlas.athena.apa.model.BooleanTicketProp;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
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

    //@After
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

//    @Test
//    public void testFindTicketsEmptyValue() {
//        HashMap<String,String> searchParams = new HashMap<String, String>();
//        searchParams.put("Seat Number", "");
//        Set<Ticket> tickets = apa.findTickets(searchParams);
//        assertEquals(0, tickets.size());
//    }
//
//   @Test
//   public void testFindTicketsUnknownPropName() {
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("UNKNOWN_PROP_NAME15", "ABCDEFG");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(0, tickets.size());
//   }
//
//   @Test
//   public void testFindTicketsInvalidValue() {
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("Seat Number", "ABCDEFG");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(0, tickets.size());
//   }
//
//   @Test
//   public void testFindTicketsOneBooleanProperty() {
//      Ticket t = new Ticket();
//
//      PropField field = new PropField();
//      field.setValueType(ValueType.BOOLEAN);
//      field.setName("BOOLEAN_PROP");
//      field.setStrict(Boolean.FALSE);
//      PropField pf = apa.savePropField(field);
//
//      BooleanTicketProp prop = new BooleanTicketProp();
//      prop.setPropField(pf);
//      prop.setValue(Boolean.TRUE);
//      t.addTicketProp(prop);
//      t = apa.saveTicket(t);
//
//      ticketsToDelete.add(t);
//      propFieldsToDelete.add(pf);
//
//      HashMap<String,String> searchParams = new HashMap<String, String>();
//      searchParams.put("BOOLEAN_PROP", "true");
//      Set<Ticket> tickets = apa.findTickets(searchParams);
//      assertEquals(1, tickets.size());
//   }
//
//   @Test
//   public void testFindTicketsOneIntegerProperty() {
//      Ticket t = new Ticket();
//
//      PropField field = new PropField();
//      field.setValueType(ValueType.INTEGER);
//      field.setName("INTEGER PROP");
//      field.setStrict(Boolean.FALSE);
//      PropField pf = apa.savePropField(field);
//
//      IntegerTicketProp prop = new IntegerTicketProp();
//      prop.setPropField(pf);
//      prop.setValue(2);
//      t.addTicketProp(prop);
//      t = apa.saveTicket(t);
//
//      ticketsToDelete.add(t);
//      propFieldsToDelete.add(pf);
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("INTEGER PROP", "2");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(1, tickets.size());
//
//       for(Ticket ticket : tickets) {
//           PTicket pTicket = t.toClientTicket();
//           for(Entry<String, String> entry : pTicket.getProps().entrySet()) {
//               assertEquals(entry.getKey(), "INTEGER PROP");
//               assertEquals(entry.getValue(), "2");
//           }
//       }
//   }
//
//   @Test
//   public void testFindTicketsTwoProperties() {
//
//      Ticket t = new Ticket();
//
//      PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "SEAT_NUMBER", StrictType.NOT_STRICT));
//      PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "LOCKED", StrictType.NOT_STRICT));
//
//      t.addTicketProp(new IntegerTicketProp(pf, 3));
//      t.addTicketProp(new BooleanTicketProp(pf2, Boolean.TRUE));
//      t = apa.saveTicket(t);
//
//      ticketsToDelete.add(t);
//      propFieldsToDelete.add(pf);
//      propFieldsToDelete.add(pf2);
//
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("SEAT_NUMBER", "3");
//       searchParams.put("LOCKED", "true");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(1, tickets.size());
//   }


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
       assertEquals(1, tickets.size());
   }

   //Testing with two good properties and one bad one
//   @Test
//   public void testFindTicketsMultipleProperties2() throws ParseException {
//
//      Ticket t = new Ticket();
//
//      PropField field = new PropField();
//      field.setValueType(ValueType.INTEGER);
//      field.setName("Seat Number");
//      field.setStrict(Boolean.FALSE);
//      PropField pf = apa.savePropField(field);
//      field = new PropField();
//      field.setValueType(ValueType.BOOLEAN);
//      field.setName("locked");
//      field.setStrict(Boolean.FALSE);
//      PropField pf2 = apa.savePropField(field);
//      field = new PropField();
//      field.setValueType(ValueType.STRING);
//      field.setName("Artist");
//      field.setStrict(Boolean.FALSE);
//      PropField pf3 = apa.savePropField(field);
//      field = new PropField();
//      field.setValueType(ValueType.DATETIME);
//      field.setName("Date");
//      field.setStrict(Boolean.FALSE);
//      PropField pf4 = apa.savePropField(field);
//
//      t.addTicketProp(new IntegerTicketProp(pf, 3));
//      t.addTicketProp(new BooleanTicketProp(pf2, Boolean.FALSE));
//      t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t.addTicketProp(new DateTimeTicketProp(pf4, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t = apa.saveTicket(t);
//
//      ticketsToDelete.add(t);
//      propFieldsToDelete.add(pf);
//      propFieldsToDelete.add(pf2);
//      propFieldsToDelete.add(pf3);
//      propFieldsToDelete.add(pf4);
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("Seat Number", "3");
//       searchParams.put("locked", "true");
//       searchParams.put("Artist", "Foo");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//
//       assertEquals(0, tickets.size());
//   }
//
//   @Test
//   public void testFindMultipleTickets() {
//
//      Ticket t = new Ticket();
//      Ticket t2 = new Ticket();
//      Ticket t3 = new Ticket();
//      Ticket t4 = new Ticket();
//
//      PropField field = new PropField(ValueType.STRING, "Artist", Boolean.FALSE);
//      PropField pf3 = apa.savePropField(field);
//
//      t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t.setName("1");
//      t = apa.saveTicket(t);
//
//      t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t2.setName("2");
//      t2 = apa.saveTicket(t2);
//
//      t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t3.setName("3");
//      t3 = apa.saveTicket(t3);
//
//      t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t4.setName("4");
//      t4 = apa.saveTicket(t4);
//
//      ticketsToDelete.add(t);
//      ticketsToDelete.add(t2);
//      ticketsToDelete.add(t3);
//      ticketsToDelete.add(t4);
//      propFieldsToDelete.add(pf3);
//
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("Artist", "ACDC");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(4, tickets.size());
//
//       Set<Integer> intNamesFound = new HashSet<Integer>();
//       for(Ticket ticket : tickets) {
//           Integer intName = Integer.parseInt(ticket.getName());
//           if(intNamesFound.contains(intName)) {
//               fail("Found the same ticket twice");
//           } else {
//                intNamesFound.add(intName);
//           }
//           switch(intName) {
//               case 1: assertTrue(IdAdapter.isEqual(t.getId(), ticket.getId())); break;
//               case 2: assertTrue(IdAdapter.isEqual(t2.getId(), ticket.getId())); break;
//               case 3: assertTrue(IdAdapter.isEqual(t3.getId(), ticket.getId())); break;
//               case 4: assertTrue(IdAdapter.isEqual(t4.getId(), ticket.getId())); break;
//               default: fail("Found a ticket that shouldn't be there"); break;
//           }
//       }
//   }
//
//   /*
//    * Six tickets in the DB.  Looking for four
//    */
//   @Test
//   public void testFindMultipleTickets2() {
//
//      Ticket t = new Ticket();
//      Ticket t2 = new Ticket();
//      Ticket t3 = new Ticket();
//      Ticket t4 = new Ticket();
//      Ticket t5 = new Ticket();
//      Ticket t6 = new Ticket();
//
//      PropField field = new PropField(ValueType.STRING, "Artist", Boolean.FALSE);
//      PropField pf3 = apa.savePropField(field);
//
//      t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t.setName("1");
//      t = apa.saveTicket(t);
//
//      t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t2.setName("2");
//      t2 = apa.saveTicket(t2);
//
//      t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t3.setName("3");
//      t3 = apa.saveTicket(t3);
//
//      t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t4.setName("4");
//      t4 = apa.saveTicket(t4);
//
//      t5.addTicketProp(new StringTicketProp(pf3, "Warrant"));
//      t5.setName("5");
//      t5 = apa.saveTicket(t5);
//
//      t6.addTicketProp(new StringTicketProp(pf3, "Warrant"));
//      t6.setName("6");
//      t6 = apa.saveTicket(t6);
//
//      ticketsToDelete.add(t);
//      ticketsToDelete.add(t2);
//      ticketsToDelete.add(t3);
//      ticketsToDelete.add(t4);
//      ticketsToDelete.add(t5);
//      ticketsToDelete.add(t6);
//      propFieldsToDelete.add(pf3);
//
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("Artist", "ACDC");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(4, tickets.size());
//
//       Set<Integer> intNamesFound = new HashSet<Integer>();
//       for(Ticket ticket : tickets) {
//           Integer intName = Integer.parseInt(ticket.getName());
//           if(intNamesFound.contains(intName)) {
//               fail("Found the same ticket twice");
//           } else {
//                intNamesFound.add(intName);
//           }
//           switch(intName) {
//               case 1: assertTrue(IdAdapter.isEqual(t.getId(), ticket.getId())); break;
//               case 2: assertTrue(IdAdapter.isEqual(t2.getId(), ticket.getId())); break;
//               case 3: assertTrue(IdAdapter.isEqual(t3.getId(), ticket.getId())); break;
//               case 4: assertTrue(IdAdapter.isEqual(t4.getId(), ticket.getId())); break;
//               default: fail("Found a ticket that shouldn't be there"); break;
//           }
//       }
//   }
//
//   /*
//    * Six tickets in the DB each with two properties.  Looking for three
//    */
//   @Test
//   public void testFindMultipleTickets3() {
//
//      Ticket t = new Ticket();
//      Ticket t2 = new Ticket();
//      Ticket t3 = new Ticket();
//      Ticket t4 = new Ticket();
//      Ticket t5 = new Ticket();
//      Ticket t6 = new Ticket();
//
//      PropField pf3 = apa.savePropField(new PropField(ValueType.STRING, "Artist", Boolean.FALSE));
//      PropField pf4 = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));
//
//      t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t.setName("1");
//      t = apa.saveTicket(t);
//
//      t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t2.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t2.setName("2");
//      t2 = apa.saveTicket(t2);
//
//      t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t3.addTicketProp(new IntegerTicketProp(pf4, 100));
//      t3.setName("3");
//      t3 = apa.saveTicket(t3);
//
//      t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t4.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t4.setName("4");
//      t4 = apa.saveTicket(t4);
//
//      t5.addTicketProp(new StringTicketProp(pf3, "Warrant"));
//      t5.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t5.setName("5");
//      t5 = apa.saveTicket(t5);
//
//      t6.addTicketProp(new StringTicketProp(pf3, "Warrant"));
//      t6.addTicketProp(new IntegerTicketProp(pf4, 75));
//      t6.setName("6");
//      t6 = apa.saveTicket(t6);
//
//      ticketsToDelete.add(t);
//      ticketsToDelete.add(t2);
//      ticketsToDelete.add(t3);
//      ticketsToDelete.add(t4);
//      ticketsToDelete.add(t5);
//      ticketsToDelete.add(t6);
//      propFieldsToDelete.add(pf3);
//      propFieldsToDelete.add(pf4);
//
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("Artist", "ACDC");
//       searchParams.put("PRICE", "50");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(3, tickets.size());
//
//       Set<Integer> intNamesFound = new HashSet<Integer>();
//       for(Ticket ticket : tickets) {
//           Integer intName = Integer.parseInt(ticket.getName());
//           if(intNamesFound.contains(intName)) {
//               fail("Found the same ticket twice");
//           } else {
//                intNamesFound.add(intName);
//           }
//           switch(intName) {
//               case 1: assertTrue(IdAdapter.isEqual(t.getId(), ticket.getId())); break;
//               case 2: assertTrue(IdAdapter.isEqual(t2.getId(), ticket.getId())); break;
//               case 4: assertTrue(IdAdapter.isEqual(t4.getId(), ticket.getId())); break;
//               default: fail("Found a ticket that shouldn't be there"); break;
//           }
//       }
//   }
//
//   /*
//    * Six tickets in the DB each with two properties.  Looking for zero
//    */
//   @Test
//   public void testFindMultipleTickets4() {
//
//      Ticket t = new Ticket();
//      Ticket t2 = new Ticket();
//      Ticket t3 = new Ticket();
//      Ticket t4 = new Ticket();
//      Ticket t5 = new Ticket();
//      Ticket t6 = new Ticket();
//
//      PropField pf3 = apa.savePropField(new PropField(ValueType.STRING, "Artist", Boolean.FALSE));
//      PropField pf4 = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));
//
//      t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t.setName("1");
//      t = apa.saveTicket(t);
//
//      t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t2.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t2.setName("2");
//      t2 = apa.saveTicket(t2);
//
//      t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t3.addTicketProp(new IntegerTicketProp(pf4, 100));
//      t3.setName("3");
//      t3 = apa.saveTicket(t3);
//
//      t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t4.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t4.setName("4");
//      t4 = apa.saveTicket(t4);
//
//      t5.addTicketProp(new StringTicketProp(pf3, "Warrant"));
//      t5.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t5.setName("5");
//      t5 = apa.saveTicket(t5);
//
//      t6.addTicketProp(new StringTicketProp(pf3, "Warrant"));
//      t6.addTicketProp(new IntegerTicketProp(pf4, 75));
//      t6.setName("6");
//      t6 = apa.saveTicket(t6);
//
//      ticketsToDelete.add(t);
//      ticketsToDelete.add(t2);
//      ticketsToDelete.add(t3);
//      ticketsToDelete.add(t4);
//      ticketsToDelete.add(t5);
//      ticketsToDelete.add(t6);
//      propFieldsToDelete.add(pf3);
//      propFieldsToDelete.add(pf4);
//
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("Artist", "Warrant");
//       searchParams.put("PRICE", "100");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(0, tickets.size());
//
//   }
//
//   /*
//    * Six tickets in the DB each with two properties.  Looking for four
//    */
//   @Test
//   public void testFindMultipleTickets5() {
//
//      Ticket t = new Ticket();
//      Ticket t2 = new Ticket();
//      Ticket t3 = new Ticket();
//      Ticket t4 = new Ticket();
//      Ticket t5 = new Ticket();
//      Ticket t6 = new Ticket();
//
//      PropField pf3 = apa.savePropField(new PropField(ValueType.STRING, "Artist", Boolean.FALSE));
//      PropField pf4 = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));
//
//      t.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t.setName("1");
//      t = apa.saveTicket(t);
//
//      t2.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t2.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t2.setName("2");
//      t2 = apa.saveTicket(t2);
//
//      t3.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t3.addTicketProp(new IntegerTicketProp(pf4, 100));
//      t3.setName("3");
//      t3 = apa.saveTicket(t3);
//
//      t4.addTicketProp(new StringTicketProp(pf3, "ACDC"));
//      t4.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t4.setName("4");
//      t4 = apa.saveTicket(t4);
//
//      t5.addTicketProp(new StringTicketProp(pf3, "Warrant"));
//      t5.addTicketProp(new IntegerTicketProp(pf4, 50));
//      t5.setName("5");
//      t5 = apa.saveTicket(t5);
//
//      t6.addTicketProp(new StringTicketProp(pf3, "Warrant"));
//      t6.addTicketProp(new IntegerTicketProp(pf4, 75));
//      t6.setName("6");
//      t6 = apa.saveTicket(t6);
//
//      ticketsToDelete.add(t);
//      ticketsToDelete.add(t2);
//      ticketsToDelete.add(t3);
//      ticketsToDelete.add(t4);
//      ticketsToDelete.add(t5);
//      ticketsToDelete.add(t6);
//      propFieldsToDelete.add(pf3);
//      propFieldsToDelete.add(pf4);
//
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("PRICE", "50");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(4, tickets.size());
//
//       Set<Integer> intNamesFound = new HashSet<Integer>();
//       for(Ticket ticket : tickets) {
//           Integer intName = Integer.parseInt(ticket.getName());
//           if(intNamesFound.contains(intName)) {
//               fail("Found the same ticket twice");
//           } else {
//                intNamesFound.add(intName);
//           }
//           switch(intName) {
//               case 1: assertTrue(IdAdapter.isEqual(t.getId(), ticket.getId())); break;
//               case 2: assertTrue(IdAdapter.isEqual(t2.getId(), ticket.getId())); break;
//               case 4: assertTrue(IdAdapter.isEqual(t4.getId(), ticket.getId())); break;
//               case 5: assertTrue(IdAdapter.isEqual(t5.getId(), ticket.getId())); break;
//               default: fail("Found a ticket that shouldn't be there"); break;
//           }
//       }
//   }
//
//   @Test
//   public void testFindTicketsWithoutTime() throws ParseException {
//
//
//      Ticket t = new Ticket();
//      Ticket t2 = new Ticket();
//      Ticket t3 = new Ticket();
//      Ticket t4 = new Ticket();
//
//      PropField field = new PropField();
//      field = new PropField();
//      field.setValueType(ValueType.DATETIME);
//      field.setName("Date");
//      field.setStrict(Boolean.FALSE);
//      PropField pf3 = apa.savePropField(field);
//
//      t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t = apa.saveTicket(t);
//
//      t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t2 = apa.saveTicket(t2);
//
//      t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t3 = apa.saveTicket(t3);
//
//      t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t4 = apa.saveTicket(t4);
//
//
//      ticketsToDelete.add(t);
//      ticketsToDelete.add(t2);
//      ticketsToDelete.add(t3);
//      ticketsToDelete.add(t4);
//      propFieldsToDelete.add(pf3);
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("Date", "2010-10-09");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(0, tickets.size());
//   }
//
//   @Test
//   public void testFindTicketsWithoutDate() throws ParseException {
//
//
//      Ticket t = new Ticket();
//      Ticket t2 = new Ticket();
//      Ticket t3 = new Ticket();
//      Ticket t4 = new Ticket();
//
//      PropField field = new PropField();
//      field = new PropField();
//      field.setValueType(ValueType.DATETIME);
//      field.setName("Date");
//      field.setStrict(Boolean.FALSE);
//      PropField pf3 = apa.savePropField(field);
//
//      t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t = apa.saveTicket(t);
//
//      t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t2 = apa.saveTicket(t2);
//
//      t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t3 = apa.saveTicket(t3);
//
//      t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-09 16:00:00")));
//      t4 = apa.saveTicket(t4);
//
//
//      ticketsToDelete.add(t);
//      ticketsToDelete.add(t2);
//      ticketsToDelete.add(t3);
//      ticketsToDelete.add(t4);
//      propFieldsToDelete.add(pf3);
//
//       HashMap<String,String> searchParams = new HashMap<String, String>();
//       searchParams.put("Date", "16:00:00");
//       Set<Ticket> tickets = apa.findTickets(searchParams);
//       assertEquals(0, tickets.size());
//   }


}
