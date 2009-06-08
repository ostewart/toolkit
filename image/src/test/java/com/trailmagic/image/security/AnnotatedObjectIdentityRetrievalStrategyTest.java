package com.trailmagic.image.security;

import junit.framework.TestCase;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;

public class AnnotatedObjectIdentityRetrievalStrategyTest extends TestCase {
    private static final long ID = 1L;
    private static final long OTHER_ID = 2L;
    private AnnotatedObjectIdentityRetrievalStrategy strategy;

    public void testUsesIdentityAnnotation() {
        assertEquals(new ObjectIdentityImpl(SomeOtherThing.class, ID), strategy.getObjectIdentity(new AnnotatedThing()));
    }

    public void testWorksWithoutIdentityAnnotation() {
        assertEquals(new ObjectIdentityImpl(SomeOtherThing.class, OTHER_ID), strategy.getObjectIdentity(new SomeOtherThing()));
    }

    public void testUsesIdentityProxyAnnotation() {
        assertEquals(new ObjectIdentityImpl(ProxyThing.class, OTHER_ID), strategy.getObjectIdentity(new ProxiedIdentityThing()));
    }

    protected void setUp() throws Exception {
        super.setUp();
        strategy = new AnnotatedObjectIdentityRetrievalStrategy();
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

    public class ProxiedIdentityThing {
        private long id = ID;
        @IdentityProxy
        private ProxyThing other = new ProxyThing();

        public ProxyThing getOther() {
            return other;
        }

        public void setOther(ProxyThing other) {
            this.other = other;
        }

        public long getId() {
            return id;
        }
    }

    public class ProxyThing {
        private long id = OTHER_ID;

        public long getId() {
            return id;
        }
    }
}
