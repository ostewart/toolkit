package com.trailmagic.image.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.trailmagic.image.ImageGroupType;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class ImageGroupTypeUserType implements UserType {
    private static final int[] SQL_TYPES = {Types.VARCHAR};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y)
            return true;
        if (null == x || null == y)
            return false;
        return x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
        String name = resultSet.getString(names[0]);
        ImageGroupType result = null;
        if (!resultSet.wasNull()) {
            result = ImageGroupType.fromString(name);
        }
        return result;
    }

    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
        if (null == value) {
            preparedStatement.setNull(index, Types.VARCHAR);
        } else {
            preparedStatement.setString(index, value.toString());
        }
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public Class returnedClass() {
        return ImageGroupType.class;
    }
}
