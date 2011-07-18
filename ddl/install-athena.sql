/* Drop databases */
DROP DATABASE IF EXISTS `ATHENA-Test`;
DROP DATABASE IF EXISTS `ATHENA`;

/* Create databases */
CREATE DATABASE `ATHENA-Test`;
CREATE DATABASE `ATHENA`;

/* grant permissions to parakeetdb.  Replace with your database user. */
GRANT ALL ON `ATHENA-Test`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';
GRANT ALL ON `ATHENA`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';

/* Install schemas */
use `ATHENA-Test`;
source athena-ddl.sql;
use `ATHENA`;
source athena-ddl.sql;