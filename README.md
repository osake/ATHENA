# ATHENA

Management Tools for the Cultural Sector

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
* Flexibile back-end storage solutions (JPA or Mongo)

## Installation and Deployment

ATHENA builds war files which can (concievably) be deployed to any Java-compliant web container.  We've been testing with Glassfishv3 and that is the only one we're supporting at this moment.

While we are bre-Beta, please see instructions for setting up ATHENA here: https://github.com/fracturedatlas/ATHENA/wiki/Setting-up-and-Deploying-ATHENA

## Components

ATHENA is made up of components, each with seperate but related duties

* ATHENA-Tix: Ticket storage and searching engine
* ATHENA-People: A "CRM" component to store information related to people, donors, patrons, etc...
* ATHENA-Orders: Stores and retrieves orders made by your customers
* ATHENA-Payments: Thin wrapper around PAyment Processor (Braintree, PayPal) APIs
* ATHENA-Stage: Metadata about tickets such as event, perofrmance, date, time, venue information

## Architecture

ATHENA Components are built from building blocks found in "core" and "helpers"

* Core: The blocks that make up the components listed above
* Helpers: Specific pieces of functionality that relate to a specific component

## Contact the Team

* Project homepage: [http://athena.fracturedatlas.org/](http://athena.fracturedatlas.org/)
* Google Group and mailing list: [http://groups.google.com/group/athena-tix-devel/](http://groups.google.com/group/athena-tix-devel/)
* IRC channel: ##ATHENA on Freenode

## Reporting Problems

Public JIRA is coming soon

## Contributors

* [Gary Moore](https://github.com/gmoore)
* [Fintan Donaghy](https://github.com/eeifdy)
* [Micah Frost](https://github.com/mfrost)
* [Ian Guffy](https://github.com/ianguffy)