-- -----------------------------------------------------
-- Data for table `ATHENA-Tix`.`TICKETS`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO `TICKETS` (`id`, `name`) VALUES (1, 'ticket');
INSERT INTO `TICKETS` (`id`, `name`) VALUES (2, 'ticket');
INSERT INTO `TICKETS` (`id`, `name`) VALUES (3, 'ticket');
INSERT INTO `TICKETS` (`id`, `name`) VALUES (4, 'ticket');
INSERT INTO `TICKETS` (`id`, `name`) VALUES (5, 'ticket');
INSERT INTO `TICKETS` (`id`, `name`) VALUES (6, 'ticket');

COMMIT;

-- -----------------------------------------------------
-- Data for table `ATHENA-Tix`.`PROP_FIELDS`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO `PROP_FIELDS` (`id`, `name`, `strict`, `valueType`) VALUES (1, 'Seat_Number', false, 'STRING');
INSERT INTO `PROP_FIELDS` (`id`, `name`, `strict`, `valueType`) VALUES (2, 'Artist', false, 'STRING');
INSERT INTO `PROP_FIELDS` (`id`, `name`, `strict`, `valueType`) VALUES (3, 'Venue', false, 'STRING');
INSERT INTO `PROP_FIELDS` (`id`, `name`, `strict`, `valueType`) VALUES (4, 'Date', false, 'DATETIME');
INSERT INTO `PROP_FIELDS` (`id`, `name`, `strict`, `valueType`) VALUES (5, 'Price', false, 'INTEGER');
INSERT INTO `PROP_FIELDS` (`id`, `name`, `strict`, `valueType`) VALUES (6, 'Status', true, 'STRING');
INSERT INTO `PROP_FIELDS` (`id`, `name`, `strict`, `valueType`) VALUES (7, 'locked', false, 'STRING');

COMMIT;

-- -----------------------------------------------------
-- Data for table `ATHENA-Tix`.`TICKET_PROPS`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (1, 1, 1, 'INTEGER', NULL, 1, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (2, 1, 2, 'INTEGER', NULL, 2, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (3, 1, 3, 'INTEGER', NULL, 3, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (4, 1, 4, 'INTEGER', NULL, 4, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (5, 1, 5, 'INTEGER', NULL, 5, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (6, 1, 6, 'INTEGER', NULL, 6, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (7, 2, 1, 'STRING', 'ACDC', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (8, 2, 2, 'STRING', 'ACDC', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (9, 2, 3, 'STRING', 'ACDC', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (10, 2, 4,'STRING',  'ACDC', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (11, 2, 5,'STRING',  'U2', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (12, 2, 6,'STRING',  'U2', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (13, 3, 1,'STRING', 'Madison Sq Garden', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (14, 3, 2,'STRING', 'Madison Sq Garden', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (15, 3, 3,'STRING', 'Madison Sq Garden', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (16, 3, 4,'STRING', 'Madison Sq Garden', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (17, 3, 5,'STRING', 'Meadowlands', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (18, 3, 6,'STRING', 'Meadowlands', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (19, 4, 1, 'DATETIME', NULL, NULL, '2010-09-09 04:04',NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (20, 4, 2, 'DATETIME', NULL, NULL, '2010-09-09 04:04',NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (21, 4, 3, 'DATETIME', NULL, NULL, '2010-09-09 05:05',NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (22, 4, 4, 'DATETIME', NULL, NULL, '2010-10-09 16:00',NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (23, 4, 5, 'DATETIME', NULL, NULL, '2010-10-09 16:00', NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (24, 4, 6, 'DATETIME', NULL, NULL, '2010-11-09 23:59',NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (31, 6, 1, 'STRING','FREE', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (32, 6, 2, 'STRING','FREE', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (33, 6, 3, 'STRING','FREE', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (34, 6, 4, 'STRING','FREE', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (35, 6, 5, 'STRING','FREE', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (36, 6, 6, 'STRING','FREE', NULL, NULL, NULL);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (37, 7, 1, 'BOOLEAN', NULL, NULL, NULL, 1);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (38, 7, 2, 'BOOLEAN', NULL, NULL, NULL, 1);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (39, 7, 3, 'BOOLEAN', NULL, NULL, NULL, 1);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (40, 7, 4, 'BOOLEAN', NULL, NULL, NULL, 0);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (41, 7, 5, 'BOOLEAN', NULL, NULL, NULL, 0);
INSERT INTO `TICKET_PROPS` (`id`, `PROP_FIELD_ID`, `TICKET_ID`, `propType`, `valueString`, `valueInteger`, `valueDateTime`, `valueBoolean`) VALUES (42, 7, 6, 'BOOLEAN', NULL, NULL, NULL, 0);

COMMIT;

-- -----------------------------------------------------
-- Data for table `ATHENA-Tix`.`PROP_VALUES`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO `PROP_VALUES` (`id`, `PROP_FIELD_ID`, `propValue`) VALUES (1, 6, 'FREE');
INSERT INTO `PROP_VALUES` (`id`, `PROP_FIELD_ID`, `propValue`) VALUES (2, 6, 'BOOKING');
INSERT INTO `PROP_VALUES` (`id`, `PROP_FIELD_ID`, `propValue`) VALUES (3, 6, 'SOLD');

COMMIT;