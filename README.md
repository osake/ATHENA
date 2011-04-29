# ATHENA

Management Tools for the Cultural Sector.

ATHENA is an open source software framework that is intended to meet the needs of arts and cultural organizations. The first release supports basic event ticketing and donor/patron management. 

## License

Copyright 2011 Fractured Atlas.  See included LICENSE file.

## Features

* Store tickets for your events and performances
* Store information for your events and performances
* Store information on your patrons
* Store orders
* Process payments through braintree

## Technical Highlights

* Service-oriented architecture
* RESTful endpoints using JSON over HTTP
* Application authentication with Digest authentication
* Flexibile back-end storage solutions (JPA or Mongo) (comping soon)

## Installation and Deployment

ATHENA can be run as a process or as a daemon (Ubuntu only).  Please see instructions for setting up ATHENA [on the wiki](https://github.com/fracturedatlas/ATHENA/wiki)

## Components

ATHENA itself takes the form of various components. Each component is deployed as a web service which can be accessed through RESTful HTTP requests. Currently JSON is the only supported format for these web service calls, though future versions of ATHENA will likely support XML and/or other formats. 

Components are further divided in core components and helper components. Core components provide low-level gateways to the data (and related functionality) tracked by ATHENA. Helper components provide simplified support for higher-level functionality by encapsulating common multi-step scenarios into single service calls. 
ATHENA’s core components include:

* Orders: a component for storing and retrieving orders made by your customers
* Payments: a thin wrapper around payment processor (e.g., Braintree, PayPal) APIs
* People: a "CRM" component to store information related to people, donors, patrons, and other constituents
* Stage: a component for storing information on events, including performance dates and times, and venue information
* Tix: a ticket storage and searching engine

ATHENA Helpers enhance the functionality of components:

* Lock: locks tickets for a period of time while the customer is in the purchasing process (bundled with Tix)
* Relationships: establishes relationships among people and organizations
* TicketFactory: creates tickets for a performance (bundled with Tix)

## Applications

ATHENA components themselves don’t provide end-user interfaces, only web service endpoints. Therefore, to provide end-users with access to ATHENA services requires the creation of a client application. ATHENA’s service-oriented architecture means that client applications can theoretically be written in any programming language for any platform. Future releases of ATHENA will likely include standard client libraries for popular programming languages. 

## Contact the Team

* Project homepage: [http://athena.fracturedatlas.org/](http://athena.fracturedatlas.org/)
* Google Group and mailing list: [http://groups.google.com/group/athena-tix-devel/](http://groups.google.com/group/athena-tix-devel/)
* IRC channel: ##ATHENA on Freenode

## Reporting Problems

We'd love to hear it!  Please feel free to repot problems to the mailing list (above) or on our [JIRA](http://jira.fracturedatlas.org)

## Contributors

* [Gary Moore](https://github.com/gmoore)
* [Fintan Donaghy](https://github.com/eeifdy)
* [Micah Frost](https://github.com/mfrost)
* [Ian Guffy](https://github.com/ianguffy)