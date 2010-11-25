package com.trailmagic.image.security;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.Photo;
import com.trailmagic.image.Image;
import com.trailmagic.image.ImageFrame;
import junit.framework.TestCase;
import org.springframework.security.acls.domain.ObjectIdentityImpl;

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

    public void testFollowsInheritanceIdentity() {
        assertEquals(new ObjectIdentityImpl(ProxyThing.class, OTHER_ID), strategy.getObjectIdentity(new TripleProxyThing()));
    }

    public void testWorksWithImageManifestations() {
        Long imageId = 1L;
        final Photo photo = new Photo();
        photo.setId(imageId);
        final HeavyImageManifestation mf = new HeavyImageManifestation();
        mf.setId(2L);
        photo.addManifestation(mf);
        assertEquals(new ObjectIdentityImpl(Image.class, imageId), strategy.getObjectIdentity(mf));
        assertEquals(strategy.getObjectIdentity(photo), strategy.getObjectIdentity(mf));
    }

    public void testWorksWithImageFrames() {
        Long imageId = 1L;
        final Photo photo = new Photo();
        photo.setId(imageId);
        final ImageFrame frame = new ImageFrame(photo);
        assertEquals(new ObjectIdentityImpl(Image.class, imageId), strategy.getObjectIdentity(frame));
    }

    // XXX: it would be sort of good to test CGLIB generated classes as the identity, proxy, domain object, etc.

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

    @Identity(ProxiedIdentityThing.class)
    public class TripleProxyThing extends ProxiedIdentityThing {

    }

    @SuppressWarnings({"UnusedDeclaration"})
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
