<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0          http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.fracturedatlas.athena</groupId>
    <artifactId>athena-apa</artifactId>
    <name>athena-apa</name>
    <version>0.2</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.fracturedatlas.athena</groupId>
        <artifactId>athena</artifactId>
        <version>0.2</version>
    </parent>

    <properties>
        <hibernate-version>3.3.2.GA</hibernate-version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.fracturedatlas.athena</groupId>
            <artifactId>athena-util</artifactId>
            <version>${pom.version}</version>
        </dependency>
        <dependency>
            <groupId>org.fracturedatlas.athena</groupId>
            <artifactId>athena-client</artifactId>
            <version>${pom.version}</version>
        </dependency>

        <dependency>
            <groupId>cglib</groupId>
            <artifactId>cglib-nodep</artifactId>
            <version>2.2</version>
        </dependency>

        <!-- JPA -->

        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>persistence-api</artifactId>
            <version>1.0</version>
        </dependency>


        <!-- HIBERNATE -->

        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate-version}</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>${hibernate-version}</version>
            <exclusions>
                <exclusion>
                    <groupId>cglib</groupId>
                    <artifactId>cglib</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>asm</groupId>
                    <artifactId>asm</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>asm</groupId>
                    <artifactId>asm-attrs</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.5.8</version>
        </dependency>
        <dependency>
            <groupId>javassist</groupId>
            <artifactId>javassist</artifactId>
            <version>3.8.0.GA</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-proxool</artifactId>
            <version>3.3.2.GA</version>
        </dependency>

        <!-- DATABASES -->

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.6</version>
        </dependency>
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongo-java-driver</artifactId>
            <version>2.0</version>
        </dependency>
        <!--
        <dependency>
            <groupId>org.postresql</groupId>
            <artifactId>postgresql-jdbc4-connector</artifactId>
            <version>8.4-701</version>
        </dependency>
        <dependency>
            <groupId>hsqldb</groupId>
            <artifactId>hsqldb</artifactId>
            <version>1.8.0.10</version>
            <scope>test</scope>
        </dependency>
        -->
    </dependencies>

</project>