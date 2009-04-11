package com.trailmagic.image.impl;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.mockito.Mockito;
import com.trailmagic.user.UserFactory;
import com.trailmagic.user.User;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.image.*;
import com.trailmagic.util.MockSecurityUtil;

public class ImageServiceImplTest {
    private UserFactory userFactory;
    private ImageManifestationRepository imageManifestationRepository;
    private ImageService imageService;
    private ImageGroupRepository imageGroupRepository;
    private ImageRepository imageRepository;
    private ImageSecurityService imageSecurityService;
    private static final long DEFAULT_GROUP_ID = 1234L;
    private static final String TEST_USER_SCREEN_NAME = "testUser";
    private ImageGroup defaultGroup;
    private static final String TEST_ROLL_NAME = "my-awesome-roll";

    @Before
    public void setUp() {
        imageGroupRepository = Mockito.mock(ImageGroupRepository.class);
        imageRepository = Mockito.mock(ImageRepository.class);
        imageSecurityService = Mockito.mock(ImageSecurityService.class);
        userFactory = Mockito.mock(UserFactory.class);
        imageManifestationRepository = Mockito.mock(ImageManifestationRepository.class);
    }

    private void withCurrentUser(User currentUser, boolean hasDefaultGroup) {
        final MockSecurityUtil securityUtil = new MockSecurityUtil(currentUser);
        imageService = new ImageServiceImpl(imageGroupRepository, imageRepository, imageSecurityService, imageManifestationRepository, userFactory, securityUtil);
        if (hasDefaultGroup) {
            defaultGroup = setupDefaultGroup(currentUser);
        }
        Mockito.when(imageGroupRepository.getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(defaultGroup);
    }


    @Test
    public void testCreateImageWithDefaultGroup() {
        User currentUser = new User(TEST_USER_SCREEN_NAME);
        withCurrentUser(currentUser, true);
        ImageMetadata imageMetadata = setupImageMetadata();

        final Photo image = imageService.createImage(imageMetadata);

        Assert.assertEquals(currentUser, image.getOwner());
        Assert.assertNotNull(image.getId());
        Assert.assertEquals(defaultGroup, image.getRoll());

        Mockito.verify(imageGroupRepository).getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME);
        Mockito.verify(imageRepository, Mockito.times(1)).saveNew(Mockito.any(Photo.class));
        Mockito.verify(imageSecurityService).addOwnerAcl(Mockito.any(Image.class));
    }

    @Test
    public void testServiceOverridesOwner() {
        User currentUser = new User(TEST_USER_SCREEN_NAME);
        withCurrentUser(currentUser, true);

        Photo newPhoto = new Photo();
        newPhoto.setOwner(null);
        newPhoto.setName("blah");

        imageService.saveNewImage(newPhoto);

        Assert.assertEquals(currentUser, newPhoto.getOwner());
        Mockito.verify(imageRepository).saveNew(newPhoto);
        Mockito.verify(imageSecurityService).addOwnerAcl(newPhoto);
    }

    @Test
    public void testLooksUpRoll() {
        User currentUser = new User(TEST_USER_SCREEN_NAME);
        withCurrentUser(currentUser, true);
        ImageMetadata imageMetadata = setupImageMetadata();
        imageMetadata.setRollName(TEST_ROLL_NAME);
        final ImageGroup existingRoll = new ImageGroup(TEST_ROLL_NAME, currentUser, ImageGroup.Type.ROLL);
        Mockito.when(imageGroupRepository.getRollByOwnerAndName(currentUser, TEST_ROLL_NAME)).thenReturn(existingRoll);

        final Photo image = imageService.createImage(imageMetadata);

        Assert.assertEquals(existingRoll, image.getRoll());
    }

    @Test
    public void testCantCreateWithoutUser() {
        withCurrentUser(null, false);
        ImageMetadata imageMetadata = setupImageMetadata();

        try {
            imageService.createImage(imageMetadata);
            Assert.fail();
        } catch (IllegalStateException e) {
            // awesome
        }
    }

    @Test
    public void testCreatesNewDefaultGroup() {
        User currentUser = new User(TEST_USER_SCREEN_NAME);
        withCurrentUser(currentUser, false);
        ImageMetadata imageMetadata = setupImageMetadata();

        final Photo image = imageService.createImage(imageMetadata);
        final ImageGroup assignedGroup = image.getRoll();
        Assert.assertNotNull(assignedGroup);
        Assert.assertEquals(ImageGroup.DEFAULT_ROLL_NAME, assignedGroup.getName());
        Assert.assertEquals(ImageGroup.Type.ROLL, assignedGroup.getType());
    }

    private ImageGroup setupDefaultGroup(User currentUser) {
        final ImageGroup defaultGroup = new ImageGroup(ImageGroup.DEFAULT_ROLL_NAME, currentUser, ImageGroup.Type.ROLL);
        defaultGroup.setId(DEFAULT_GROUP_ID);
        return defaultGroup;
    }

    private ImageMetadata setupImageMetadata() {
        ImageMetadata imageMetadata = new ImageMetadata();
        imageMetadata.setShortName("testImage");
        imageMetadata.setDisplayName("Test Image");
        imageMetadata.setCaption("This is a test image.");
        imageMetadata.setCopyright("Copyright 2009");
        imageMetadata.setCreator("Oliver Stewart");
        return imageMetadata;
    }
}
