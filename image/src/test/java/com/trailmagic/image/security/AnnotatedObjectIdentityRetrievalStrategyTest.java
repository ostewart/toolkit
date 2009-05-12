package com.trailmagic.image.security;

import junit.framework.TestCase;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;

public class AnnotatedObjectIdentityRetrievalStrategyTest extends TestCase {
    private static final long ID = 1L;
    private static final long OTHER_ID = 2L;

    public void testUsesIdentityAnnotation() {
        final AnnotatedObjectIdentityRetrievalStrategy strategy = new AnnotatedObjectIdentityRetrievalStrategy();
        assertEquals(new ObjectIdentityImpl(SomeOtherThing.class, ID), strategy.getObjectIdentity(new AnnotatedThing()));
    }

    public void testWorksWithoutIdentityAnnotation() {
        final AnnotatedObjectIdentityRetrievalStrategy strategy = new AnnotatedObjectIdentityRetrievalStrategy();
        assertEquals(new ObjectIdentityImpl(SomeOtherThing.class, OTHER_ID), strategy.getObjectIdentity(new SomeOtherThing()));
    }

    @Identity(SomeOtherThing.class)
    public class AnnotatedThing {
        private long id = ID;

        public long getId() {
            return id;
        }
    }

    public class SomeOtherThing {
        private long id = OTHER_ID;

        public long getId() {
            return id;
        }
    }
}
