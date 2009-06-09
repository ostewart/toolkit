package com.trailmagic.image.security;

import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.util.ClassUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.io.Serializable;

public class AnnotatedObjectIdentityRetrievalStrategy implements ObjectIdentityRetrievalStrategy {
    public ObjectIdentity getObjectIdentity(Object domainObject) {
        return getObjectIdentity(domainObject, ClassUtils.getUserClass(domainObject));
    }

    private ObjectIdentity getObjectIdentity(Object domainObject, Class<?> declaredClass) {
        final Identity identity = declaredClass.getAnnotation(Identity.class);
        if (identity != null) {
            if (identity.value().isAssignableFrom(domainObject.getClass())) {
                return getObjectIdentity(domainObject, identity.value());
            }
            return identityFromIdPropertyAndIdentityType(domainObject, identity.value());
        } else {
            final Field[] fields = declaredClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(IdentityProxy.class)) {
                    return identityFromProxyIdentityField(domainObject, field);
                }
            }
            return identityFromIdPropertyAndIdentityType(domainObject, declaredClass);
        }

    }

    private ObjectIdentity identityFromProxyIdentityField(Object domainObject, Field field) {
        try {
            return getObjectIdentity(PropertyUtils.getProperty(domainObject, field.getName()));
        } catch (IllegalAccessException e) {
            throw new ObjectIdentityRetrievalException("Could not access annotated property "
                                                       + field.getName() + " from object: " + domainObject, e);
        } catch (InvocationTargetException e) {
            throw new ObjectIdentityRetrievalException("Could not access annotated property "
                                                       + field.getName() + " from object: " + domainObject, e);
        } catch (NoSuchMethodException e) {
            throw new ObjectIdentityRetrievalException("Could not access annotated property "
                                                       + field.getName() + " from object: " + domainObject, e);
        }
    }

    private ObjectIdentity identityFromIdPropertyAndIdentityType(Object domainObject, Class javaType) {
        try {
            return new ObjectIdentityImpl(javaType, (Serializable) PropertyUtils.getProperty(domainObject, "id"));
        } catch (IllegalAccessException e) {
            throw new ObjectIdentityRetrievalException("Could not access id property", e);
        } catch (InvocationTargetException e) {
            throw new ObjectIdentityRetrievalException("Could not access id property", e);
        } catch (NoSuchMethodException e) {
            throw new ObjectIdentityRetrievalException("Could not access id property", e);
        }
    }
}
