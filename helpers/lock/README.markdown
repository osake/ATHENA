ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This document is licensed under a Creative Commons Attribution 3.0 United
States License, a copy of which you should have received with this
document. If not, see http://creativecommons.org/licenses/by/3.0/us

You may share and adapt this work under the terms this license, provided
you describe the changes and attribute the original work to the copyright
holder above.

=

The lock helper provides a RESTful lock object to assist client applications with locking tickets while customers are deciding to purchase.  While tickets are locked, no other application can lock those tickets.

#Installing the lock helper

Add this in the dependencies section of your pom.xml:

        <dependency>
            <groupId>org.fracturedatlas.athena.helper</groupId>
            <artifactId>lock</artifactId>
            <version>${athena.version}</version>
        </dependency>

Of course, you'll need to specify the athena.version somewhere in your pom file

The lock helper is included in already Tix <https://github.com/fracturedatlas/ATHENA/tree/master/components/tix>

#Usage

## Properties

The lock helper will be using the following properties on tickets

* sold
* lockId
* lockedByIp - Poorly named, this actually records the user nae of your client application when security is enabled
* lockedByApiKey (unused and will be removed)
* lockExpires
* lockTimes

## Obtaining a lock on tickets

POST a new lock to {component_name}/meta/locks

    1 > POST http://localhost/tix/meta/locks
    1 > Content-Type: application/json
    1 > X-ATHENA-Key: SAMPLEAPIKEYfowihe9338833wehhfhf
    1 >
    {"tickets":["62","63"]}

Sample response

    1 < 200
    1 < Date: Tue, 12 Oct 2010 23:31:49 GMT
    1 < Content-Length: 195
    1 < server: grizzly/1.9.18
    1 < Content-Type: application/json
    1 <
    {"id":"3753ad0c-fa22-4977-80f4-d01aa4d1cf40",
     "tickets":["62","63"],
     "lockExpires":"Oct 12, 2010 7:41:49 PM",
     "lockedByApi":"[Your client applcation username]",
     "lockedByIp":"127.0.0.1",
     "status":"OK"}

## Renewing a lock on tickets (Optional)

PUT the lock to {component_name}/meta/locks/{lockId}

    2 > PUT http://localhost:9998/test/locks/3753ad0c-fa22-4977-80f4-d01aa4d1cf40
    2 > Content-Type: application/json
    2 > X-ATHENA-Key: SAMPLEAPIKEYfowihe9338833wehhfhf
    2 >
    {"tickets":["62","63"],
     "lockExpires":"Oct 12, 2010 7:41:49 PM",
     "id":"3753ad0c-fa22-4977-80f4-d01aa4d1cf40",
     "lockedByApi":"[Your client applcation username]",
     "lockedByIp":"127.0.0.1",
     "status":"RENEW"}

*It's worth noting* that this deviates from the HTTP spec in that PUTting the resource multiple times will result in different results.

Sample response

    2 < 200
    2 < Date: Tue, 12 Oct 2010 23:31:49 GMT
    2 < Content-Length: 195
    2 < server: grizzly/1.9.18
    2 < Content-Type: application/json
    2 <
    {"tickets":["62","63"],
     "lockExpires":"Oct 12, 2010 7:42:49 PM",
     "id":"3753ad0c-fa22-4977-80f4-d01aa4d1cf40",
     "lockedByApi":"[Your client applcation username]",
     "lockedByIp":"127.0.0.1",
     "status":"OK"}

## Selling the tickets and close the lock

PUT the lock to {component_name}/meta/locks/{lockId}

    3 > PUT http://localhost:9998/test/locks/3753ad0c-fa22-4977-80f4-d01aa4d1cf40
    3 > Content-Type: application/json
    3 > X-ATHENA-Key: SAMPLEAPIKEYfowihe9338833wehhfhf
    3 >
    {"tickets":["62","63"],
     "lockExpires":"Oct 12, 2010 7:42:49 PM",
     "id":"3753ad0c-fa22-4977-80f4-d01aa4d1cf40",
     "lockedByApi":"[Your client applcation username]",
     "lockedByIp":"127.0.0.1",
     "status":"COMPLETE"}

Sample response

    3 < 200
    3 < Date: Tue, 12 Oct 2010 23:31:49 GMT
    3 < Content-Length: 195
    3 < server: grizzly/1.9.18
    3 < Content-Type: application/json
    3 <
    {"tickets":["62","63"],
     "lockExpires":"Oct 12, 2010 7:42:49 PM",
     "id":"3753ad0c-fa22-4977-80f4-d01aa4d1cf40",
     "lockedByApi":"[Your client applcation username]",
     "lockedByIp":"127.0.0.1",
     "status":"OK"}

## Delete the lock without checking out (optional)

DELETE {component_name}/meta/locks/{lockId}

## Getting information about the lock (optional)

GET {component_name}/meta/locks/{lockId}
