package com.trailmagic.image.security;

import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.Photo;
import com.trailmagic.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.sid.GrantedAuthoritySid;
import org.springframework.security.acls.sid.Sid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"ThrowableInstanceNeverThrown"})
public class SpringSecurityImageSecurityServiceTest {
    private SpringSecurityImageSecurityService service;
    @Mock private MutableAclService aclService;
    @Mock private ImageGroupRepository imageGroupRepository;
    @Mock private ObjectIdentityRetrievalStrategy identityRetrievalStrategy;
    private MutableAcl publicAcl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new SpringSecurityImageSecurityService(aclService, imageGroupRepository, identityRetrievalStrategy);
    }

    @Test
    public void testIsPublic() {
        final Photo photo = makeTestPhoto();

        setupAclWithPublic(photo, true);

        assertTrue(service.isPublic(photo));
    }

    @Test
    public void testIsNotPublic() {
        final Photo photo = makeTestPhoto();

        setupAclWithPublic(photo, false);

        assertFalse(service.isPublic(photo));
    }

    @Test
    public void testNoAceIsNotPublic() {
        final Photo photo = makeTestPhoto();

        final ObjectIdentityImpl identity = new ObjectIdentityImpl(photo);
        Mockito.when(identityRetrievalStrategy.getObjectIdentity(photo)).thenReturn(identity);
        final GrantedAuthoritySid everyone = new GrantedAuthoritySid("ROLE_EVERYONE");

        publicAcl = Mockito.mock(MutableAcl.class);
        Mockito.when(aclService.readAclById(identity, new Sid[]{everyone})).thenReturn(publicAcl);
        Mockito.when(publicAcl.isGranted(new Permission[]{BasePermission.READ}, new Sid[]{everyone}, false)).thenThrow(new NotFoundException("not found"));

        assertFalse(service.isPublic(photo));
    }

    @Test
    public void testNoAclIsNotPublic() {
        final Photo photo = makeTestPhoto();

        final ObjectIdentityImpl identity = new ObjectIdentityImpl(photo);
        Mockito.when(identityRetrievalStrategy.getObjectIdentity(photo)).thenReturn(identity);
        final GrantedAuthoritySid everyone = new GrantedAuthoritySid("ROLE_EVERYONE");

        Mockito.when(aclService.readAclById(identity, new Sid[]{everyone})).thenThrow(new NotFoundException("not found"));

        assertFalse(service.isPublic(photo));
    }

    @Test
    public void testSaveImageManifestationUsesImageAcl() {

    }


    private Photo makeTestPhoto() {
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
}
