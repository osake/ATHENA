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

/**
 * Since the APA layer is datastore-agnostic, "id" fields must be declared as type: Object.  This class
 * defines "id" as a Long for JpaApaAdapter and Hibernate.  See annotations over "Object id" in the model classes
 */
package org.fracturedatlas.athena.apa.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongUserType implements UserType {


  private static final int[] SQL_TYPES = {Types.BIGINT};

  public int[] sqlTypes() {
    return SQL_TYPES;
  } 

  public Class returnedClass() {
    return Long.class;
  }

  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == y) {
      return true;
    } else if (x == null || y == null) {
      return false;
    } else {
      return x.equals(y);
    }
  }

  public int hashCode(Object value) throws HibernateException {
    return value.hashCode();
  }

  public String toString(Object value) {
    Long l = (Long) value;
    return Long.toString(l);
  }

  public Object nullSafeGet(ResultSet resultSet,
          String[] names, Object owner)
          throws HibernateException, SQLException {
    Long result = null;
    Long id = resultSet.getLong(names[0]);
    if (!resultSet.wasNull()) {
      result = new Long(id);
    }
    return result;
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index)
          throws HibernateException, SQLException {
    if (value == null) {
      statement.setNull(index, Types.NULL);
    } else {
      Long id = (Long) value;

      statement.setLong(index, id);
    }
  }

  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  public boolean isMutable() {
    return false;
  }

  /** @see org.hibernate.usertype.UserType#replace(Object, Object, Object)     */
  public Object replace(Object original, Object target, Object owner) {
    return original;
  }

  /** @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, Object)     */
  public Object assemble(Serializable cached, Object owner) {
    return cached;
  }

  /** @see org.hibernate.usertype.UserType#disassemble(Object)     */
  public Serializable disassemble(Object value) throws HibernateException {
    return massageToLong(value);
  }

  public static Long massageToLong(Object value) throws HibernateException {
    Long retValue = null;
    if(value == null) {
      return null;
    }
    if (value instanceof java.lang.Long) {
      retValue = (Long) value;
    } else if (value instanceof java.lang.Integer) {
      Integer tempI = (Integer) value;
      retValue = tempI.longValue();
    } else if (value instanceof java.lang.String) {
      try {
        retValue = Long.valueOf(value.toString());
      } catch (ClassCastException e) {
        Logger logger = LoggerFactory.getLogger(LongUserType.class);
        logger.error(e.getMessage(), e);
        retValue = null;
      } catch (NumberFormatException e) {
        Logger logger = LoggerFactory.getLogger(LongUserType.class);
        retValue = null;
      }
    } else {
      throw new UnsupportedOperationException("Can't convert " + value.getClass() + " to an id");
    }
    return retValue;
  }
}
