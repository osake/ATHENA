ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This document is licensed under a Creative Commons Attribution 3.0 United
States License, a copy of which you should have received with this
document. If not, see http://creativecommons.org/licenses/by/3.0/us

You may share and adapt this work under the terms this license, provided
you describe the changes and attribute the original work to the copyright
holder above.

=

The docs helper provides a RESTFul resource for storing text files, links, and binary files.

#Usage

## Properties

* organizationId - The organization that this document belongs to
* title - The document title
* type - The document type, available types are: article, file, link
* permissionLevel - The permission level of the document: public, private, executive.  Note that these permissions are enforced in the client, not in Athena
* isPublic - A boolean property marking this document as public
* sharedWith - An array or organizationIds that this document is available to
* description - A description of the document
* body - The actual text of an ARTICLE document, the link of a link document, or the location of a file document

## Creating a document

POST a document to /docs.  Include the following information.

    POST /docs
    {
      "organizationId": "3",
      "title": "First Document",
      "type": "article",
      "permissionLevel": "executive",
      "isPublic": false,
      "sharedWith": [],
      "description": "A sample document",
      "body": "The document text is here"
    }
    
Example response:

    {
      "id": "3aef4",
      "createdAt": 2011-04-05T04:04:04Z,
      "updatedAt": 2011-04-05T04:04:54Z,
      "organizationId": "3",
      "title": "First Document",
      "type": "article",
      "permissionLevel": "executive",
      "isPublic": false,
      "sharedWith": [],
      "description": "A sample document",
      "body": "The document text is here"
    }    
    
When creating a link, just put the link HREF into the "body" field

POST a document to /docs.  Include the following information.

    POST /docs
    {
      "organizationId": "3",
      "title": "First Document",
      "type": "link",
      "permissionLevel": "executive",
      "isPublic": false,
      "sharedWith": [],
      "description": "A sample document",
      "body": "http://www.example.com/example.html"
    }
    
## Creating a binary document

First, POST a new document with type "FILE" and a blank "body" field using the example above.  Then PUT the file put multipart/form-data mime type.

    PUT /docs/{id}
    
## Updating an existing document

PUT the document to /docs/{id}.  Include the following information.

    POST /docs/3aef4
    {
      "id": "3aef4",
      "organizationId": "3",
      "title": "First Document",
      "type": "article",
      "permissionLevel": "executive",
      "isPublic": false,
      "sharedWith": [],
      "description": "A sample document",
      "body": "The document text is here.  It has been updated to reflect new changes"
    }
    
Example response:

    {
      "id": "3aef4",
      "createdAt": 2011-04-05T04:04:04Z,
      "updatedAt": 2011-04-05T04:15:54Z,
      "organizationId": "3",
      "title": "First Document",
      "type": "article",
      "permissionLevel": "executive",
      "isPublic": false,
      "sharedWith": [],
      "description": "A sample document",
      "body": "The document text is here.  It has been updated to reflect new changes"
    } 
    
## Getting a document

    GET /docs/3aef4
    
Example response:

    {
      "id": "3aef4",
      "createdAt": 2011-04-05T04:04:04Z,
      "updatedAt": 2011-04-05T04:15:54Z,
      "organizationId": "3",
      "title": "First Document",
      "type": "article",
      "permissionLevel": "executive",
      "isPublic": false,
      "sharedWith": [],
      "description": "A sample document",
      "body": "The document text is here.  It has been updated to reflect new changes"
    } 

Example response for type FILE

    {
      "id": "3aef4",
      "createdAt": 2011-04-05T04:04:04Z,
      "updatedAt": 2011-04-05T04:15:54Z,
      "organizationId": "3",
      "title": "First Document",
      "type": "file",
      "permissionLevel": "executive",
      "isPublic": false,
      "sharedWith": [],
      "description": "A sample document",
      "body": "http://www.example.com/files/file.pdf"
    } 

## Searching for documents

Searching adheres to normal ATHENA conventions.

Get all documents with EXECUTIVE permissions

    GET /docs?permissionLevel=executive
    
Get all documents created after June 4

    GET /docs?createdAt=gt2011-06-04T00:00:00Z

## Getting documents of a certain type

You can append the type to the GET string for convenience

    GET /docs/articles
    
And also append search terms to refine further

    GET /docs/articles?permissionLevel=executive
    
## Searching the index

All fields are indexed.  Search the index with the _q operator

    GET /docs?_q=searchquery
    
## Deleting a document

    DELETE /docs/3aef4
    
Example response:
  
    204 - No Content
    
