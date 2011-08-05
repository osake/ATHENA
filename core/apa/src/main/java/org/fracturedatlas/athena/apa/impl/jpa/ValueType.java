package org.fracturedatlas.athena.apa.impl.jpa;


public enum ValueType {
    
	BOOLEAN {
            public TicketProp newTicketProp() {
                return booleanTicketProp;
            }
        },
	DATETIME {
            public TicketProp newTicketProp() {
                return dateTimeTicketProp;
            }
        },
	INTEGER {
            public TicketProp newTicketProp() {
                return integerTicketProp;
            }
        },
	TEXT {
            public TicketProp newTicketProp() {
                return textTicketProp;
            }
        },
	STRING {
            public TicketProp newTicketProp() {
                return stringTicketProp;
            }
        };
        private static TicketProp booleanTicketProp = new BooleanTicketProp();
        private static TicketProp dateTimeTicketProp = new DateTimeTicketProp();
        private static TicketProp integerTicketProp = new IntegerTicketProp();
        private static TicketProp textTicketProp = new TextTicketProp();
        private static TicketProp stringTicketProp = new StringTicketProp();

        public abstract TicketProp newTicketProp();
}
