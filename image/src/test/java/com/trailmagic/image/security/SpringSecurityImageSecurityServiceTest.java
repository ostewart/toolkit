package com.trailmagic.image.security;

import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.Photo;
import com.trailmagic.user.User;
import junit.framework.TestCase;
import org.mockito.Mockito;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.sid.Sid;
import org.springframework.security.acls.sid.GrantedAuthoritySid;
import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;

public class SpringSecurityImageSecurityServiceTest extends TestCase {
    private SpringSecurityImageSecurityService service;
    private MutableAclService aclService;
    private ObjectIdentityRetrievalStrategy identityRetrievalStrategy;

    public void testIsPublic() {
        final Photo photo = new Photo();
        photo.setId(1L);
        photo.setName("test");
        photo.setOwner(new User("test"));

        setupAclWithPublic(photo, true);

        assertTrue(service.isPublic(photo));
    }

    public void testIsNotPublic() {
        final Photo photo = new Photo();
        photo.setId(1L);
        photo.setName("test");
        photo.setOwner(new User("test"));

        setupAclWithPublic(photo, false);

        assertFalse(service.isPublic(photo));
    }

    private void setupAclWithPublic(Photo photo, boolean isPublic) {
        final ObjectIdentityImpl identity = new ObjectIdentityImpl(photo);
        Mockito.when(identityRetrievalStrategy.getObjectIdentity(photo)).thenReturn(identity);
        final Acl acl = Mockito.mock(Acl.class);
        final GrantedAuthoritySid everyone = new GrantedAuthoritySid("ROLE_EVERYONE");
        Mockito.when(acl.isGranted(new Permission[]{BasePermission.READ}, new Sid[]{everyone}, false)).thenReturn(isPublic);
        Mockito.when(aclService.readAclById(identity, new Sid[]{everyone})).thenReturn(acl);
    }


//
//    public void testMasks() {
//        assertEquals(SimpleAclEntry.READ_WRITE_CREATE_DELETE | SimpleAclEntry.ADMINISTRATION, new CumulativePermission().set(BasePermission.READ).set(BasePermission.WRITE).set(BasePermission.CREATE).set(BasePermission.DELETE).set(BasePermission.ADMINISTRATION).getMask());
//    }
//    public void testRead() {
//        assertEquals(SimpleAclEntry.READ, BasePermission.READ.getMask());
//    }
//    public void testWrite() {
//        assertEquals(SimpleAclEntry.WRITE, BasePermission.WRITE.getMask());
//    }
//    public void testCreate() {
//        assertEquals(SimpleAclEntry.CREATE, BasePermission.CREATE.getMask());
//    }
//    public void testDelete() {
//        assertEquals(SimpleAclEntry.DELETE, BasePermission.DELETE.getMask());
//    }
//    public void testAdmin() {
//        assertEquals(SimpleAclEntry.ADMINISTRATION, BasePermission.ADMINISTRATION.getMask());
//    }
//
//    public void testCumulative() {
//        assertEquals(SimpleAclEntry.ADMINISTRATION, new CumulativePermission().set(BasePermission.ADMINISTRATION).getMask());

    //    }
    protected void setUp() throws Exception {
        super.setUp();
        aclService = Mockito.mock(MutableAclService.class);
        final ImageGroupRepository repository = Mockito.mock(ImageGroupRepository.class);

        identityRetrievalStrategy = Mockito.mock(ObjectIdentityRetrievalStrategy.class);
        service = new SpringSecurityImageSecurityService(aclService, repository, identityRetrievalStrategy);
    }
}
