ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This document is licensed under a Creative Commons Attribution 3.0 United
States License, a copy of which you should have received with this
document. If not, see http://creativecommons.org/licenses/by/3.0/us

You may share and adapt this work under the terms this license, provided
you describe the changes and attribute the original work to the copyright
holder above.

========

Within ATHENA people, there exists the concept of people objects relating to other people objects.  This helper allows a client to retrieve all relationships associated with an object in one call.

+Relationships schema+

pre. leftSideId:
    valueType: STRING
    strict: false
  relationshipType:
    valueType: STRING
    strict: false
  rightSideId:
    valueType: STRING
    strict: false
  inverseType:
    valueType: STRING
    strict: false

+Example+

Consider that object 1 is the father of object 3.  The ATHENA record for this relationship would be:

pre. {"leftSideId":"1",
      "relationshipType":"Father",
      "rightSideId":"3",
      "inverseType":"Son",
      "id":"6"}
      
This would read that "1 is the father of 3".  The inverseType field reads that "3 is the son of 1".  Clients are responsible for setting both sides of the relationship.

+Example Request+

This request will return relationships that involved object id=2, where 1 appears in leftSideId OR rightSideId

pre. GET /meta/relationships/people/2

pre. [{"relationshipType":"Brother",
  "rightSideId":"3",
  "leftSideId":"2",
  "inverseType":"Brother",
  "id":"7"},
  {"relationshipType":"Father",
  "rightSideId":"2",
  "leftSideId":"1",
  "inverseType":"Son",
  "id":"5"}]