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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class AthenaRunner {
    
    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);

        WebAppContext tix = new WebAppContext();
        tix.setContextPath("/tix");
        tix.setWar("/Users/gary/Documents/apps/ATHENA/components/tix/target/tix.war");

        WebAppContext admin = new WebAppContext();
        admin.setContextPath("/audit");
        admin.setWar("/Users/gary/Documents/apps/ATHENA/components/audit-server/target/audit.war");

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { admin, tix });
        server.setHandler(contexts);

        server.start();
        server.join();
    }
}
