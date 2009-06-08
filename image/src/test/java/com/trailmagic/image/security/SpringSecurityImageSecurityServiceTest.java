package com.trailmagic.image.security;

import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.Photo;
import com.trailmagic.user.User;
import junit.framework.TestCase;
import org.mockito.Mockito;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.sid.GrantedAuthoritySid;
import org.springframework.security.acls.sid.Sid;

@SuppressWarnings({"ThrowableInstanceNeverThrown"})
public class SpringSecurityImageSecurityServiceTest extends TestCase {
    private SpringSecurityImageSecurityService service;
    private MutableAclService aclService;
    private ObjectIdentityRetrievalStrategy identityRetrievalStrategy;
    private MutableAcl publicAcl;

    public void testIsPublic() {
        final Photo photo = testPhoto();

        setupAclWithPublic(photo, true);

        assertTrue(service.isPublic(photo));
    }

    public void testIsNotPublic() {
        final Photo photo = testPhoto();

        setupAclWithPublic(photo, false);

        assertFalse(service.isPublic(photo));
    }

    public void testNoAceIsNotPublic() {
        final Photo photo = testPhoto();

        final ObjectIdentityImpl identity = new ObjectIdentityImpl(photo);
        Mockito.when(identityRetrievalStrategy.getObjectIdentity(photo)).thenReturn(identity);
        final GrantedAuthoritySid everyone = new GrantedAuthoritySid("ROLE_EVERYONE");

        publicAcl = Mockito.mock(MutableAcl.class);
        Mockito.when(aclService.readAclById(identity, new Sid[]{everyone})).thenReturn(publicAcl);
        Mockito.when(publicAcl.isGranted(new Permission[]{BasePermission.READ}, new Sid[]{everyone}, false)).thenThrow(new NotFoundException("not found"));

        assertFalse(service.isPublic(photo));
    }

    public void testNoAclIsNotPublic() {
        final Photo photo = testPhoto();

        final ObjectIdentityImpl identity = new ObjectIdentityImpl(photo);
        Mockito.when(identityRetrievalStrategy.getObjectIdentity(photo)).thenReturn(identity);
        final GrantedAuthoritySid everyone = new GrantedAuthoritySid("ROLE_EVERYONE");

        Mockito.when(aclService.readAclById(identity, new Sid[]{everyone})).thenThrow(new NotFoundException("not found"));

        assertFalse(service.isPublic(photo));
    }

    private Photo testPhoto() {
        final Photo photo = new Photo();
        photo.setId(1L);
        photo.setName("test");
        photo.setOwner(new User("test"));
        return photo;
    }

    private void setupAclWithPublic(Photo photo, boolean isPublic) {
        final ObjectIdentityImpl identity = new ObjectIdentityImpl(photo);
        Mockito.when(identityRetrievalStrategy.getObjectIdentity(photo)).thenReturn(identity);
        final GrantedAuthoritySid everyone = new GrantedAuthoritySid("ROLE_EVERYONE");

        publicAcl = Mockito.mock(MutableAcl.class);
        Mockito.when(aclService.readAclById(identity, new Sid[]{everyone})).thenReturn(publicAcl);

        Mockito.when(publicAcl.isGranted(new Permission[]{BasePermission.READ}, new Sid[]{everyone}, false)).thenReturn(isPublic);
    }

    protected void setUp() throws Exception {
        super.setUp();
        aclService = Mockito.mock(MutableAclService.class);
        final ImageGroupRepository repository = Mockito.mock(ImageGroupRepository.class);

        identityRetrievalStrategy = Mockito.mock(ObjectIdentityRetrievalStrategy.class);
        service = new SpringSecurityImageSecurityService(aclService, repository, identityRetrievalStrategy);
    }
}
