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
package org.fracturedatlas.athena.runner;

import java.io.FileInputStream;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;

public class AthenaRunner {

    public static void main(String[] args) throws Exception {
        String port = System.getProperty("port");

        Server server = new Server(Integer.parseInt(port));
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        String[] appNames = new String[]{"tix", 
        //"stage", "people", "orders", "payments",
        "audit"};

        for (String appName : appNames) {
            WebAppContext app = new WebAppContext();
            app.setContextPath("/" + appName);
            app.setExtraClasspath("../components/shared/config/;../components/shared/lib/web-resources.jar;../components/shared/lib/util.jar;../components/shared/lib/apa.jar;../components/shared/lib/client.jar;../components/" + appName + "/config/");

            app.setWar("../components/" + appName + "/war/" + appName + ".war");
            app.setLogUrlOnStart(true);
            contexts.addHandler(app);
        }

        server.setHandler(contexts);

        XmlConfiguration configuration = new XmlConfiguration(new FileInputStream("../config/jetty.xml"));
        configuration.configure(server);
        server.start();

        server.start();

        System.out.println("#");
        System.out.println("#");
        System.out.println("# Athena started on port " + port + ".  Have fun.");
        System.out.println("#");
        System.out.println("#");

        server.join();


    }
}
