package org.fracturedatlas.athena.apa.model;


public enum ValueType {
	BOOLEAN {
            public TicketProp newTicketProp() {
                return new BooleanTicketProp();
            }
        },
	DATETIME {
            public TicketProp newTicketProp() {
                return new DateTimeTicketProp();
            }
        },
	INTEGER {
            public TicketProp newTicketProp() {
                return new IntegerTicketProp();
            }
        },
	STRING {
            public TicketProp newTicketProp() {
                return new StringTicketProp();
            }
        };

        public abstract TicketProp newTicketProp();
}
