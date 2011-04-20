package org.fracturedatlas.athena.apa.impl.jpa;


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
	TEXT {
            public TicketProp newTicketProp() {
                return new TextTicketProp();
            }
        },
	STRING {
            public TicketProp newTicketProp() {
                return new StringTicketProp();
            }
        };

        public abstract TicketProp newTicketProp();
}
