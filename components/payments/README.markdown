ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This document is licensed under a Creative Commons Attribution 3.0 United
States License, a copy of which you should have received with this
document. If not, see http://creativecommons.org/licenses/by/3.0/us

You may share and adapt this work under the terms this license, provided
you describe the changes and attribute the original work to the copyright
holder above.

# Authorize a payment

The only required elements are:
- amount
- creditCard.cardNumber
- creditCard.expirationDate

*Minimum Request*
    POST /payments/transactions/authorize
    {
        "amount":"22.00",
        "creditCard":{
            "cardNumber":"4111111111111111",
            "expirationDate":"05/2013"
            }
    }

*Full Request*
    POST /payments/transactions/authorize
    {
        "amount":"10.00",
        "orderId":"order id",
        "creditCard":{
            "cardNumber":"4111111111111111",
            "expirationDate":"05/2012",
            "cardholderName":"Joe Q Ticketbuyer",
            "cvv":"229"
            },
        "customer":{
            "firstName":"Joe",
            "lastName":"Ticketbuyer",
            "company":"Optional",
            "phone":"312-555-1234",
            "fax":"312-555-1235",
            "email":"joe@example.com"
            },
        "billingAddress":{
            "firstName":"Suzy",
            "lastName":"Shipto",
            "company":"Fractured Atlas",
            "streetAddress1":"248 W 35th St",
            "streetAddress2":"FLOOR 10",
            "city":"New York",
            "state":"New York",
            "postalCode":"10001",
            "country":"US"
            },
        "shippingAddress":{
            "firstName":"Suzy",
            "lastName":"Shipto",
            "company":"Fractured Atlas",
            "streetAddress1":"248 W 35th St",
            "streetAddress2":"FLOOR 10",
            "streetAddress3":"",
            "city":"New York",
            "state":"New York",
            "postalCode":"10001",
            "country":"US"
            }
    }

*Responses*

Success

    {"success":true,"transactionId":"6efb6f62-d009-4f82-845e-4cc30b53c768","message":"optional"}

Failure

    {"success":true,"transactionId":"6efb6f62-d009-4f82-845e-4cc30b53c768","message":"(message)"}

*Saving a credit card for future use*

Add this to the request

    ...
    "storeCard":"true"
    ...

And receive this in the response

    ..."creditCard":{"token":"j2rt"},"customer":{"id":"620006"}...

Save that credit card token for future use.

# Authorize a payment using a stored credit card

    POST /payments/transactions/authorize
    {
        "amount":"10.00",
        "orderId":"order id",
        "creditCard":{
            "token":"j2rt"
        }
    }

# Settle a payment

*Request*

    POST /payments/transactions/settle
    {
        "transactionId": "92778d05-dc7f-426b-95b6-61781c1c9708",
        "amount":"40.00"
    }

If you are settling for the full amount, then amount is not necessary.

*Responses*

    {"success":true,"transactionId":"6efb6f62-d009-4f82-845e-4cc30b53c768","message":"optional"}

# Refund a payment

*Request*

    POST /payments/transactions/refund
    {
        "transactionId": "92778d05-dc7f-426b-95b6-61781c1c9708"
    }

You cannot partially refund a payment

*Responses*

    {"success":true,"transactionId":"6efb6f62-d009-4f82-845e-4cc30b53c768","message":"optional"}

# Void a payment

*Request*

    POST /payments/transactions/void
    {
        "transactionId": "92778d05-dc7f-426b-95b6-61781c1c9708"
    }

You cannot partially void a payment

*Responses*

    {"success":true,"transactionId":"6efb6f62-d009-4f82-845e-4cc30b53c768","message":"optional"}

#CRUD a Customer

*Save a customer*

*Request*

    POST /payments/customers
    {"firstName":"Joe","lastName":"Tester","company":"Bad Company","phone":"410-909-9090","email":"test@test.com"}

*Response*

    {"firstName":"Joe","lastName":"Tester","company":"Bad Company","phone":"410-909-9090","email":"test@test.com","id":"823904"}

*Update a customer*

*Request*

    PUT /payments/customers/{id}
    {"firstName":"Jose","lastName":"Tester","company":"Bad Company","phone":"410-909-9090","email":"test@test.com","id":"823904"}

*Response*

    {"firstName":"Jose","lastName":"Tester","company":"Bad Company","phone":"410-909-9090","email":"test@test.com","id":"823904"}

*Get a customer*

This request will include all cards associated with the customer

*Request*

    GET /payments/customers/{id}

*Response*

    {"firstName":"Jose","lastName":"Tester","company":"Bad Company","phone":"410-909-9090","email":"test@test.com","id":"823904","creditCards":[{"cardNumber":"401200******7777","expirationDate":"09/2011","cardholderName":"Joeseph L Cool","token":"qr9p","id":"qr9p"},{"cardNumber":"411111******1111","expirationDate":"05/2011","cardholderName":"Joe Cool","token":"9sxd","id":"9sxd"}]}

*Delete a customer*

*Request*

    DELETE /payments/customers/{id}
    (No body is necessary)

*Response*

    204 - No Content

#Saving a Credit Card outside of a transaction

Both credit cards and customers respond to a resource-based RESTful interface.

Credit cards are related to customers.  A credit card cannot exist without being attached to a customer.  So, to save a card, first save the customer.

*Save a card*

*Request*

    POST /payments/cards
    {"cardNumber":"4111111111111111","expirationDate":"05/2011","cardholderName":"Joe Cool","customer":{"id":"285161"}}

You can send the entire customer if needed, but only the id is necessary

*Response*

    {"cardNumber":"411111******1111","expirationDate":"05/2011","cardholderName":"Joe Cool","token":"hdgt","id":"hdgt","customer":{"id":"285161"}}

*Update a card*

*Request*

    PUT /payments/cards/{id}
    {"cardNumber":"411111******1111","expirationDate":"12/2012","cardholderName":"Joe Cool","token":"9f9p","id":"9f9p","customer":{"id":"485254"}}

*Response*

    {"cardNumber":"411111******1111","expirationDate":"12/2012","cardholderName":"Joe Cool","token":"9f9p","id":"9f9p","customer":{"id":"485254"}}

*Get a card*

*Request*

    GET /payments/cards/{id}

*Response*

    {"cardNumber":"411111******1111","expirationDate":"12/2012","cardholderName":"Joe Cool","token":"9f9p","id":"9f9p","customer":{"id":"485254"}}

*Delete a card*

*Request*

    DELETE /payments/cards/{id}
    (No body is necessary)

*Response*

    204 - No Content