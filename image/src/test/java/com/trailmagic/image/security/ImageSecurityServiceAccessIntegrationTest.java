package com.trailmagic.image.security;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.Photo;
import com.trailmagic.image.impl.ImageInitializer;
import com.trailmagic.image.security.test.DataCreator;
import com.trailmagic.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-global.xml",
                                   "classpath:com/trailmagic/image/applicationContext-test.xml",
                                   "classpath:applicationContext-user.xml",
                                   "classpath:applicationContext-imagestore.xml",
                                   "classpath:applicationContext-imagestore-authorization.xml"})
@Transactional
public class ImageSecurityServiceAccessIntegrationTest {
    @Autowired private ImageSecurityService securityService;
    @Autowired private ImageInitializer imageInitializer;
    @Autowired private DataCreator dataCreator;
    @Autowired private ImageRepository imageRepository;
    @Autowired private ImageGroupRepository imageGroupRepository;

    @Test(expected = AccessDeniedException.class)
    public void testAddOwnerAclToImageFailsWithoutUser() throws IOException {
        dataCreator.authenticateAnonymousUser();
        securityService.addOwnerAcl(new Image());
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddOwnerAclToImageGroupFailsWithoutUser() throws IOException {
        dataCreator.authenticateAnonymousUser();
        securityService.addOwnerAcl(new ImageGroup("name", new User("owner"), ImageGroup.Type.ROLL));
    }

    @Test(expected = AccessDeniedException.class)
    public void testMakePublicDeniedWithoutUser() throws IOException {
        dataCreator.authenticateAnonymousUser();
        securityService.makePublic(new Image());
    }

    @Test(expected = AccessDeniedException.class)
    public void testRegularUserHasNoAccessAfterPhotoCreation() {
        final Photo photo = createPhotoAsTestUser();

        authenticateAsRegularUser();

        assertNull(imageRepository.getById(photo.getId()));
    }

    @Test(expected = AccessDeniedException.class)
    public void testRegularUserHasNoAccessAfterImageGroupCreation() {
        final ImageGroup imageGroup = createImageGroupAsTestUser();

        authenticateAsRegularUser();

        imageGroupRepository.getById(imageGroup.getId());
    }

    @Test
    public void testAnonymousUserHasAccessAfterMakePublic() {
        final Photo photo = createPhotoAsTestUser();
        securityService.makePublic(photo);

        dataCreator.authenticateAnonymousUser();

        assertEquals(photo, imageRepository.getById(photo.getId()));
    }

    @Test(expected = AccessDeniedException.class)
    public void testAnonymousUserHasNoAccessAfterMakePrivate() {
        final Photo photo = createPhotoAsTestUser();
        try {
            securityService.makePublic(photo);
            securityService.makePrivate(photo);
        } catch (AccessDeniedException e) {
            fail("Caught AccessDeniedException before expectation");
        }

        dataCreator.authenticateAnonymousUser();

        imageRepository.getById(photo.getId());
    }

    @Test(expected = AccessDeniedException.class)
    public void testNonOwnerDeniedFromMakePublic() {
        final Photo photo = createPhotoAsTestUser();

        authenticateAsRegularUser();

        securityService.makePublic(photo);
    }

    @Test(expected = AccessDeniedException.class)
    public void testNonOwnerDeniedFromMakePrivate() {
        final Photo photo = createPhotoAsTestUser();

        authenticateAsRegularUser();

        securityService.makePrivate(photo);
    }

    @Test(expected = AccessDeniedException.class)
    public void testNonOwnerDeniedFromAddReadPermissionToUser() {
        final Photo photo = createPhotoAsTestUser();

        authenticateAsRegularUser();

        securityService.addReadPermission(photo, new User("luser"));
    }

    @Test(expected = AccessDeniedException.class)
    public void testNonOwnerDeniedFromAddReadPermissionToRole() {
        final Photo photo = createPhotoAsTestUser();

        authenticateAsRegularUser();

        securityService.addReadPermission(photo, "ROLE_EVERYONE");
    }

    private void authenticateAsRegularUser() {
        final User regularUser = dataCreator.createTestUser("luser");
        dataCreator.authenticateUserWithAuthorities(regularUser, "ROLE_USER");
    }


    private Photo createPhotoAsTestUser() {
        try {
            final User owner = dataCreator.createTestUser();

            dataCreator.authenticateUserWithAuthorities(owner, "ROLE_USER");
            return dataCreator.makePhoto("foo.jpg", true, owner);
        } catch (AccessDeniedException e) {
            fail("AccessDenied caught in data setup");
            throw new IllegalStateException("AccessDenied caught in data setup");
        }
    }

    private ImageGroup createImageGroupAsTestUser() {
        try {
            final User owner = dataCreator.createTestUser();

            dataCreator.authenticateUserWithAuthorities(owner, "ROLE_USER");
            return dataCreator.makeRoll(owner, true);
        } catch (AccessDeniedException e) {
            fail("AccessDenied caught in data setup");
            throw new IllegalStateException("AccessDenied caught in data setup");
        }
    }
}
