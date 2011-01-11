/*

ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/

 */
package org.fracturedatlas.athena.admin;

import java.io.Console;
import java.util.Arrays;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AthenaAdmin {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("USAGE: admin [command]");
            System.out.println("Where [command] is one of: create-user");
            System.exit(1);
        }

        Console c = System.console();
        if (c == null) {
            System.err.println("No console.");
            System.exit(1);
        }

        String login = c.readLine("Enter new username: ");
        Boolean match = false;
        char[] password = null;
        char[] confirmedPassword = null;
        while(!match) {
            password = c.readPassword("Enter password: ");
            confirmedPassword = c.readPassword("Enter password again: ");
            match = Arrays.equals(password, confirmedPassword);
            if(!match) {
               c.format("Passwords do not match please try again");
               c.format("\n");
            }
        }

        System.out.println("yaa");

        ApplicationContext context = new ClassPathXmlApplicationContext("security.xml");

        java.util.Arrays.fill(password, ' ');
        java.util.Arrays.fill(confirmedPassword, ' ');


        System.out.println("yaa!!!!!!!!!!!!!!");
    }
}
