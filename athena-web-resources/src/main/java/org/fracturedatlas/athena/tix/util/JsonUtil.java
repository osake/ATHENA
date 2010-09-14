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
package org.fracturedatlas.athena.tix.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class JsonUtil {

    static GsonBuilder gb = new GsonBuilder()
                                .setLongSerializationPolicy(LongSerializationPolicy.STRING);

    static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = gb.create();
        }

        return gson;
    }
     
    static ObjectMapper mapper;

  
    public static ObjectMapper getMapper() {
        if(mapper == null) {
        	mapper = new ObjectMapper();
        	AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        	// make deserializer use JAXB annotations (only)
        	mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        	// make serializer use JAXB annotations (only)
        	mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
        }
        return mapper;
    }
    
}
