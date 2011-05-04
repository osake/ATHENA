ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This document is licensed under a Creative Commons Attribution 3.0 United
States License, a copy of which you should have received with this
document. If not, see http://creativecommons.org/licenses/by/3.0/us

You may share and adapt this work under the terms this license, provided
you describe the changes and attribute the original work to the copyright
holder above.

=

# Bulk

The bulk helper allows clients to update multiple records in one JSON request

#Installing the helper

Add this in the dependencies section of your pom.xml:

        <dependency>
            <groupId>org.fracturedatlas.athena.helper</groupId>
            <artifactId>bulk</artifactId>
            <version>${athena.version}</version>
        </dependency>

Of course, you'll need to specify the athena.version somewhere in your pom file

The bulk helper is included in already Tix <https://github.com/fracturedatlas/ATHENA/tree/master/components/tix>

#Usage

## Properties

The bulk helper relies on no special fields in tix

## Updating multiple tickets

Put all ids to be updated on the URL.  In the JSON body, include fields to be updated.  Other fields will be unchanged.

The HTTP PATCH method is better suited for this, however support for PATCH is not yet widespread: http://tools.ietf.org/html/rfc5789

    PUT http://localhost/tix/bulk/tickets/3;4
    {
      "status":"on_sale"
    }
    
Response:

    [
    {
      "id":3,
      "eventId": "3221",
      "performanceId": "3009",
      "price": 22,
      "status":"on_sale"
    },
    {
      "id":4,
      "eventId": "3221",
      "performanceId": "3009",
      "price": 22,
      "status":"on_sale"
    }
    ]

The update will be rejected, and no records modified, if:

* Any of the ids in the URL do not exist
* Any field to be updated does not exist
* The value(s) are not valid because of strictness or typing