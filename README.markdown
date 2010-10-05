ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This document is licensed under a Creative Commons Attribution 3.0 United
States License, a copy of which you should have received with this
document. If not, see http://creativecommons.org/licenses/by/3.0/us

You may share and adapt this work under the terms this license, provided
you describe the changes and attribute the original work to the copyright
holder above.

========

ATHENA is a modular service-oriented application.  The modules in ATHENA can be used to be arts-infrascturcure software applications.  ATHENA implementations for Tix, People, and Orders can be found here: http://github.com/fracturedatlas/ATHENA-Components

#Downloading and installing ATHENA

You'll need a MySQL database stood up to run the tests.  DB name: "ATHENA-Tix", u:parakeetdb, p:parakeetdb.  These faules can be changed in the file

*src/main/resources/proxool.properties*

Once MySQL is set up and listening, do this:

  git clone git@github.com:fracturedatlas/ATHENA.git
  cd ATHENA
  cd athena-apa
  mvn test
  cd ../athena-web-resources
  mvn test -Dtest=ContainerSuite
  mvn install -DskipTests=true