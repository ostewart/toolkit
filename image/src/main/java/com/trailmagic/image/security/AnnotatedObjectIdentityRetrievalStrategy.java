package com.trailmagic.image.security;

import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.io.Serializable;

public class AnnotatedObjectIdentityRetrievalStrategy implements ObjectIdentityRetrievalStrategy {
    public ObjectIdentity getObjectIdentity(Object domainObject) {
        final Identity identity = domainObject.getClass().getAnnotation(Identity.class);
        if (identity != null) {
            try {
                return new ObjectIdentityImpl(identity.value(), (Serializable) PropertyUtils.getProperty(domainObject, "id"));
            } catch (IllegalAccessException e) {
                throw new ObjectIdentityRetrievalException("Could not access id property", e);
            } catch (InvocationTargetException e) {
                throw new ObjectIdentityRetrievalException("Could not access id property", e);
            } catch (NoSuchMethodException e) {
                throw new ObjectIdentityRetrievalException("Could not access id property", e);
            }
        } else {
            final Field[] fields = domainObject.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(IdentityProxy.class)) {
                    try {
                        return getObjectIdentity(PropertyUtils.getProperty(domainObject, field.getName()));
                    } catch (IllegalAccessException e) {
                        throw new ObjectIdentityRetrievalException("Could not access annotated property " + field.getName(), e);
                    } catch (InvocationTargetException e) {
                        throw new ObjectIdentityRetrievalException("Could not access annotated property " + field.getName(), e);
                    } catch (NoSuchMethodException e) {
                        throw new ObjectIdentityRetrievalException("Could not access annotated property " + field.getName(), e);
                    }
                }
            }
            return new ObjectIdentityImpl(domainObject);
        }
    }
}
