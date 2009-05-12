package com.trailmagic.image.security;

import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
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
            return new ObjectIdentityImpl(domainObject);
        }
    }
}
