package com.trailmagic.image.security;

import com.trailmagic.image.ImageMetadata;
import com.trailmagic.image.ImageService;
import com.trailmagic.image.Photo;
import com.trailmagic.image.impl.ImageInitializer;
import com.trailmagic.image.security.test.DataCreator;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-global.xml",
                                   "classpath:com/trailmagic/image/applicationContext-test.xml",
                                   "classpath:applicationContext-user.xml",
                                   "classpath:applicationContext-imagestore.xml",
                                   "classpath:applicationContext-imagestore-authorization.xml"})
@Transactional
public class ImageServiceAccessIntegrationTest {
    @Autowired private ImageSecurityService service;
    @Autowired private ImageService imageService;
    @Autowired private UserRepository userRepository;
    @Autowired private ImageInitializer imageInitializer;
    @Autowired private DataCreator dataCreator;
    private static final ByteArrayInputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[]{});

    @Test(expected = AccessDeniedException.class)
    public void testCreateDefaultImageFailsWithoutUser() throws IOException {
        dataCreator.authenticateAnonymousUser();
        imageService.createDefaultImage("foo.jpg");
    }

    @Test(expected = AccessDeniedException.class)
    public void testCreateImageFailsWithoutUser() throws IOException {
        dataCreator.authenticateAnonymousUser();
        imageService.createImage(new ImageMetadata());
    }

    @Test
    public void testCreateDefaultImageSucceedsWithUser() throws IOException {
        final User testUser = dataCreator.createTestUser();
        dataCreator.authenticateUserWithAuthorities(testUser, "ROLE_USER");
        imageService.createDefaultImage("foo.jpg");
    }

    @Test(expected = AccessDeniedException.class)
    public void testCreateImageAtPositionFailsWithoutUser() throws IOException {
        dataCreator.authenticateAnonymousUser();
        imageService.createImageAtPosition("foo.jpg", EMPTY_INPUT_STREAM, 1);
    }

    @Test
    public void testCreateImageAtPositionSucceedsWithUser() throws IOException {
        User testUser = dataCreator.createTestUser();
        dataCreator.authenticateUserWithAuthorities(testUser, "ROLE_USER");
        imageService.createImageAtPosition("foo.jpg", EMPTY_INPUT_STREAM, 1);
    }

    @Test(expected = AccessDeniedException.class)
    public void testCreateManifestationsFailsWithoutUser() throws IOException {
        dataCreator.authenticateAnonymousUser();
        User owner = dataCreator.createTestUser();
        final Photo photo = dataCreator.makePhoto("foo.jpg", false, owner);
        imageService.createManifestations(photo, EMPTY_INPUT_STREAM);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddImageToGroupWithPositionFailsWithoutUser() {
        dataCreator.authenticateAnonymousUser();
        User owner = dataCreator.createTestUser();
        imageService.addImageToGroup(dataCreator.makePhoto("foo.jpg", false, owner),
                                     dataCreator.makeRoll(owner, false), 1);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddImageToGroupFailsWithoutUser() {
        dataCreator.authenticateAnonymousUser();
        User owner = dataCreator.createTestUser();
        imageService.addImageToGroup(dataCreator.makePhoto("foo.jpg", false, owner), dataCreator.makeRoll(owner, false));
    }

    @Test(expected = AccessDeniedException.class)
    public void testSetImageGroupPreviewFailsWithoutUser() {
        dataCreator.authenticateAnonymousUser();
        imageService.setImageGroupPreview(1, 2);
    }
}
