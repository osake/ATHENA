ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This document is licensed under a Creative Commons Attribution 3.0 United
States License, a copy of which you should have received with this
document. If not, see http://creativecommons.org/licenses/by/3.0/us

You may share and adapt this work under the terms this license, provided
you describe the changes and attribute the original work to the copyright
holder above.

========

ATHENA is a modular service-oriented application.  The modules in ATHENA can be used to be arts-infrascturcure software applications.  ATHENA implementations for Tix, People, and Orders can be found here: <http://github.com/fracturedatlas/ATHENA-Components>

#Downloading and installing ATHENA

Download and install Maven, the build tool used by ATHENA.  <http://maven.apache.org/>

You'll need a MySQL database stood up to run the tests.  DB name: "ATHENA-Tix", u:parakeetdb, p:parakeetdb.  These vaules can be changed in the file

*src/main/resources/proxool.properties*

Once MySQL is set up and listening, do this:

	git clone git@github.com:fracturedatlas/ATHENA.git
	cd ATHENA
	cd apa
	mvn test
	cd ../web-resources
	mvn test -Dtest=ContainerSuite
	mvn install -DskipTests=true
	
##ATHENA architecture

ATHENA is made up of Maven sub-projects or "modules".

###apa

The Data Access layer of ATHENA

###web-resources

The Jersey RESTful front end and the business logic that sits behind it.

###util

Utilities that are shared across all modules

###sdk

A POM that includes several other modules so that projects can include all needed athena dependencies with one pom dependency

###client

Client library for consuming ATHENA resources from Java clients.

##Including ATHENA in your project

Add this dependency to your projects POM file

	<dependency>
	    <groupId>org.fracturedatlas.athena</groupId>
	    <artifactId>sdk</artifactId>
	    <version>${athena-version}</version>
	    <type>pom</type>
	</dependency>
	
And add these two repositories

	<repositories>
	    <repository>
	        <id>fractured-atlas-releases</id>
	        <name>Fractured Atlas Releases</name>
	        <url>http://nexus.fracturedatlas.org:8081/nexus/content/repositories/releases/</url>
	        <layout>default</layout>
	        <snapshots>
	            <enabled>false</enabled>
	        </snapshots>
	        <releases>
	            <enabled>true</enabled>
	            <updatePolicy>never</updatePolicy>
	            <checksumPolicy>ignore</checksumPolicy>
	        </releases>
	    </repository>
	    <repository>
	        <id>fractured-atlas-snapshots</id>
	        <name>Fractured Atlas Snapshots</name>
	        <url>http://nexus.fracturedatlas.org:8081/nexus/content/repositories/snapshots/</url>
	        <layout>default</layout>
	        <snapshots>
	            <enabled>true</enabled>
	            <checksumPolicy>ignore</checksumPolicy>
	        </snapshots>
	        <releases>
	            <enabled>false</enabled>
	        </releases>
	    </repository>
	</repositories>

##Problems? Questions?

We'll be happy to help.  Please contact us on our developer list <http://groups.google.com/group/athena-tix-devel> Or on Freenode in ##athena.