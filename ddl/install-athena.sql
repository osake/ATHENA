/* Drop databases */
DROP DATABASE IF EXISTS `ATHENA-Test`;
DROP DATABASE IF EXISTS `ATHENA-Tix`;
DROP DATABASE IF EXISTS `ATHENA-Stage`;
DROP DATABASE IF EXISTS `ATHENA-People`;
DROP DATABASE IF EXISTS `ATHENA-Orders`;
DROP DATABASE IF EXISTS `ATHENA-Security`;
DROP DATABASE IF EXISTS `ATHENA-Audit`;

/* Create databases */
CREATE DATABASE `ATHENA-Test`;
CREATE DATABASE `ATHENA-Tix`;
CREATE DATABASE `ATHENA-Stage`;
CREATE DATABASE `ATHENA-People`;
CREATE DATABASE `ATHENA-Orders`;
CREATE DATABASE `ATHENA-Security`;
CREATE DATABASE `ATHENA-Audit`;

/* grant permissions to parakeetdb */
GRANT ALL ON `ATHENA-Test`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';
GRANT ALL ON `ATHENA-Tix`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';
GRANT ALL ON `ATHENA-Stage`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';
GRANT ALL ON `ATHENA-People`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';
GRANT ALL ON `ATHENA-Orders`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';
GRANT ALL ON `ATHENA-Security`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';
GRANT ALL ON `ATHENA-Audit`.* TO parakeetdb@localhost IDENTIFIED BY 'parakeetdb';

/* Install schemas */
use `ATHENA-Test`;
source athena-ddl.sql;
use `ATHENA-Tix`;
source athena-ddl.sql;
use `ATHENA-Stage`;
source athena-ddl.sql;
use `ATHENA-People`;
source athena-ddl.sql;
use `ATHENA-Orders`;
source athena-ddl.sql;

use `ATHENA-Security`;
source athena-security.sql;
use `ATHENA-Audit`;
source athena-audit.sql;