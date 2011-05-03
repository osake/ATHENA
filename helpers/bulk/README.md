ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This document is licensed under a Creative Commons Attribution 3.0 United
States License, a copy of which you should have received with this
document. If not, see http://creativecommons.org/licenses/by/3.0/us

You may share and adapt this work under the terms this license, provided
you describe the changes and attribute the original work to the copyright
holder above.

=

The codes helper provides a RESTFul resource for setting codes on tickets.  A "code" allows a certain ticket price to be made available to a user.

#Installing the helper

Add this in the dependencies section of your pom.xml:

        <dependency>
            <groupId>org.fracturedatlas.athena.helper</groupId>
            <artifactId>codes</artifactId>
            <version>${athena.version}</version>
        </dependency>

Of course, you'll need to specify the athena.version somewhere in your pom file

The codes helper is included in already Tix <https://github.com/fracturedatlas/ATHENA/tree/master/components/tix>

#Usage

## Properties

The codes helper relies on the following fields in Tix

* code - The actual discount code
* description - A human-readable description of the code
* startDate - A datetime.  Codes will not be considered valid before this date.
* endDate - A datetime.  Codes will not be considered valid after this date.
* enabled - Boolean.  True if this code is enabled, false otherwise.  Codes that have enabled set to false are considered not valid regardless of startDate and endDate

## Creating a code

POST a code to /tix/codes.  Include the following information.  performances and events are optional.

    POST http://localhost/tix/codes
    {
      "code": "festivalpass1",
      "description": "A human readable description",
      "performances": [],
      "events": [],
      "tickets": [],
      "startDate": "2010-03-04T05:05:30-04:00",
      "endDate":"2010-03-04T05:05:30-04:00",
      "price": 50,
      "enabled": true
    }
    
The only required field is "code".  "code" must be unique. 
    
Some notes on the fields that haven't already been discussed above:

      "tickets": An array of tickets that this code will apply to.
      
Using the following requires use of the ATHENA Stage component

      "performances": An array of performances that this code will apply to.  Codes will retrieve the list of tickets attached to each performance and apply the code to those tickets.
      "events": An array of events that this code will apply to.  Codes will retrieve the list of tickets attached to each event and apply the code to those tickets.
      
Example response:

    {
      "id": 39029,
      "code": "festivalpass1",
      "description": "A human readable description",
      "tickets": [(An array of tickets that this code has been applied to)],
      "startDate": "2010-03-04T05:05:30-04:00",
      "endDate":"2010-03-04T05:05:30-04:00",
      "price": 50,
      "enabled": true
    }
    
## Getting information about a code

    GET http://localhost/tix/codes/{code_id}
    
Response:

    {
      "id": 39029,
      "code": "festivalpass1",
      "description": "A human readable description",
      "tickets": [(An array of tickets that this code has been applied to)],
      "startDate": "2010-03-04T05:05:30-04:00",
      "endDate":"2010-03-04T05:05:30-04:00",
      "price": 50,
      "enabled": 
    }    
    
## Updating a code

Make a PUT request similar to the POST request above.

    PUT http://localhost/tix/codes/{code_id}
    {
      "id": "39029",
      "code": "festivalpass1",
      "description": "A human readable description",
      "performances": [],
      "events": [],
      "tickets": [],
      "startDate": "2010-03-04T05:05:30-04:00",
      "endDate":"2010-03-04T05:05:30-04:00",
      "price": 50,
      "eligibleClients": [],
      "enabled": 
    }

*You cannot update the "code" field*

All other fields will be updated EXCEPT: performances, events, and tickets.  Any tickets in this list will have this code applied to them.  Tickets not in this list will not be updated.

Example response:

    {
      "id": 39029,
      "code": "festivalpass1",
      "description": "A human readable description",
      "tickets": [(An array of tickets that this code has been applied to)],
      "startDate": "2010-03-04T05:05:30-04:00",
      "endDate":"2010-03-04T05:05:30-04:00",
      "price": 50,
      "enabled": 
    }
    
## Removing a code from ticket

    DELETE http://localhost/tix/codes/{code_id}/tickets/{ticket_id}
    
Example response:

    204 - No content
    
## Deleting a code entirely

    DELETE http://localhost/tix/codes/{code_id}
    
Example response:

    204 - No content
    
## Retrieving tickets with a code

    GET http://localhost/tix/codes/{id}/tickets?...
    
ATHENA will respond with eligible tickets if any are found.  Both the "price" field and the coded price will be returned.

If no tickets are found, an empty array will be returned.