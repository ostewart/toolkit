package com.trailmagic.image.impl;

import com.trailmagic.image.*;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.image.security.SecurityTestHelper;
import com.trailmagic.resizer.ImageResizeService;
import com.trailmagic.resizer.ResizeFailedException;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.util.SecurityUtil;
import com.trailmagic.util.TimeSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class ImageServiceImplTest {
    private ImageService imageService;
    @Mock private UserRepository userRepository;
    @Mock private ImageManifestationRepository imageManifestationRepository;
    @Mock private ImageGroupRepository imageGroupRepository;
    @Mock private ImageRepository imageRepository;
    @Mock private ImageSecurityService imageSecurityService;
    @Mock private ImageInitializer imageInitializer;
    @Mock private SecurityUtil securityUtil;
    @Mock private TimeSource timeSource;
    @Mock private ImageResizeService imageResizeService;
    @Mock private HibernateUtil hibernateUtil;
    @Mock private ImageManifestationService imageManifestationService;
    @Mock private ImageResizeClient imageResizeClient;
    private static final long MANIFESTATION_ID = 1234L;
    private static final long DEFAULT_GROUP_ID = MANIFESTATION_ID;
    private static final Date TEST_TIME = new Date();
    private ImageGroup defaultGroup;
    private static final String TEST_ROLL_NAME = "my-awesome-roll";
    private final SecurityTestHelper securityTestHelper = new SecurityTestHelper();
    private User testUser;
    private static final ByteArrayInputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[]{});
    private static final String FIRST_NAME = "Testy";
    private static final String LAST_NAME = "McTesterton";
    private static final int TEST_YEAR = 2010;
    private static final int ARBITRARY_POSITION = 62;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testUser = new User("testy");
        testUser.setFirstName(FIRST_NAME);
        testUser.setLastName(LAST_NAME);
        when(timeSource.today()).thenReturn(TEST_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.set(TEST_YEAR, Calendar.FEBRUARY, 28);
        when(timeSource.calendar()).thenReturn(calendar);
        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        imageService = new ImageServiceImpl(imageGroupRepository, imageRepository, imageSecurityService, userRepository, securityUtil, imageInitializer, timeSource, imageResizeClient);
    }

    private void withCurrentUser(User currentUser, boolean hasDefaultGroup) {
        if (hasDefaultGroup) {
            defaultGroup = setupDefaultGroup(currentUser);
        }
        when(imageGroupRepository.getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(defaultGroup);
    }


    @Test
    public void testCreateImageWithDefaultGroup() {
        User currentUser = testUser;
        withCurrentUser(currentUser, true);
        ImageMetadata imageMetadata = setupImageMetadata();

        final Photo image = imageService.createImage(imageMetadata);

        assertEquals(currentUser, image.getOwner());
        assertNotNull(image.getId());
        assertEquals(defaultGroup, image.getRoll());

        verify(imageGroupRepository).getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME);
        verify(imageInitializer, Mockito.times(1)).saveNewImage(Mockito.any(Photo.class));
    }


    @Test
    public void testLooksUpRoll() {
        User currentUser = testUser;
        withCurrentUser(currentUser, true);
        ImageMetadata imageMetadata = setupImageMetadata();
        imageMetadata.setRollName(TEST_ROLL_NAME);
        final ImageGroup existingRoll = new ImageGroup(TEST_ROLL_NAME, currentUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.getRollByOwnerAndName(currentUser, TEST_ROLL_NAME)).thenReturn(existingRoll);

        final Photo image = imageService.createImage(imageMetadata);

        assertEquals(existingRoll, image.getRoll());
    }


    @Test
    public void testCreatesNewDefaultGroup() {
        User currentUser = testUser;
        withCurrentUser(currentUser, false);
        ImageMetadata imageMetadata = setupImageMetadata();

        final Photo image = imageService.createImage(imageMetadata);
        final ImageGroup assignedGroup = image.getRoll();
        assertNotNull(assignedGroup);
        assertEquals(ImageGroup.DEFAULT_ROLL_NAME, assignedGroup.getName());
        assertEquals(ImageGroup.Type.ROLL, assignedGroup.getType());
    }

    @Test
    public void testSavesImageMetaData() throws IOException, SQLException, ResizeFailedException {
        securityTestHelper.disableSecurityInterceptor();
        withCurrentUser(testUser, true);

        ImageMetadata imageMetadata = setupImageMetadata();

        final ImageManifestation originalManifestation = new ImageManifestation();
        originalManifestation.setId(MANIFESTATION_ID);

        final Photo photo = imageService.createImage(imageMetadata);

        assertEquals(imageMetadata.getCaption(), photo.getCaption());
        assertEquals(imageMetadata.getCopyright(), photo.getCopyright());
        assertEquals(imageMetadata.getCreator(), photo.getCreator());
        assertEquals(imageMetadata.getDisplayName(), photo.getDisplayName());
        assertEquals(imageMetadata.getShortName(), photo.getName());

        verify(imageInitializer).saveNewImage(photo);
        verify(imageResizeClient, never()).createOriginalManifestation(Mockito.<Image>any(), eq(EMPTY_INPUT_STREAM));
        verify(imageResizeClient, never()).createResizedManifestations(Mockito.<Image>any(), Mockito.<SecurityContext>any());
        verify(imageGroupRepository).saveGroup(Mockito.<ImageGroup>any());
    }

    @Test
    public void testSetsDefaultMetaData() throws IOException {
        Photo image = imageService.createDefaultImage("foo.jpg");

        assertMatchesDefaultMetadata(image);
        assertEquals("foo.jpg", image.getName());
    }

    private void assertMatchesDefaultMetadata(Photo image) {
        assertEquals("Copyright " + TEST_YEAR, image.getCopyright());
        assertEquals(String.format("%s %s", FIRST_NAME, LAST_NAME), image.getCreator());
        assertEquals(ImageGroup.DEFAULT_ROLL_NAME, image.getRoll().getName());
    }

    @Test
    public void testCreatesManifestations() throws IOException {
        Photo image = new Photo("foo", testUser);
        imageService.createManifestations(image, EMPTY_INPUT_STREAM);

        verify(imageResizeClient).createOriginalManifestation(image, EMPTY_INPUT_STREAM);
        verify(imageResizeClient).createResizedManifestations(eq(image), Mockito.<SecurityContext>any());
    }

    @Test
    public void testSetsDefaultMetaDataWithPosition() throws IOException {
        Photo image = imageService.createImageAtPosition("foo.jpg", EMPTY_INPUT_STREAM, ARBITRARY_POSITION);

        verify(imageResizeClient, never()).createOriginalManifestation(image, EMPTY_INPUT_STREAM);
        verify(imageResizeClient, never()).createResizedManifestations(eq(image), Mockito.<SecurityContext>any());

        assertMatchesDefaultMetadata(image);

        assertEquals(ARBITRARY_POSITION, image.getRoll().getFrames().first().getPosition());
        assertEquals("foo.jpg", image.getName());
    }

    @Test
    public void testFindOrCreateRollCreatesDefaultRoll() {
        when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(null);

        ImageGroup roll = imageService.findNamedOrDefaultRoll(null, testUser);

        verify(imageInitializer).saveNewImageGroup(any(ImageGroup.class));

        assertEquals(ImageGroup.DEFAULT_ROLL_NAME, roll.getName());
        assertEquals("Uploads", roll.getDisplayName());
        assertEquals("Uploaded Images", roll.getDescription());
        assertEquals(ImageGroup.Type.ROLL, roll.getType());
        assertEquals(testUser, roll.getOwner());
        assertEquals(TEST_TIME, roll.getUploadDate());
        assertNull(roll.getSupergroup());
    }

    @Test
    public void testFindOrCreateRollFindsDefaultRoll() {
        ImageGroup expectedGroup = new ImageGroup("test", testUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(expectedGroup);

        ImageGroup roll = imageService.findNamedOrDefaultRoll(null, testUser);

        verify(imageInitializer, never()).saveNewImageGroup(any(ImageGroup.class));

        assertEquals(expectedGroup, roll);
    }

    @Test
    public void testFindOrCreateRollFindsDefaultRollWithEmptyString() {
        ImageGroup expectedGroup = new ImageGroup("test", testUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(expectedGroup);

        ImageGroup roll = imageService.findNamedOrDefaultRoll("", testUser);

        verify(imageInitializer, never()).saveNewImageGroup(any(ImageGroup.class));

        assertEquals(expectedGroup, roll);
    }

    @Test
    public void testFindOrCreateRollFindsNamedRoll() {
        ImageGroup expectedGroup = new ImageGroup("test", testUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.getRollByOwnerAndName(testUser, TEST_ROLL_NAME)).thenReturn(expectedGroup);

        ImageGroup roll = imageService.findNamedOrDefaultRoll(TEST_ROLL_NAME, testUser);

        verify(imageInitializer, never()).saveNewImageGroup(any(ImageGroup.class));

        assertEquals(expectedGroup, roll);
    }

    @Test(expected = ImageGroupNotFoundException.class)
    public void testFindOrCreateRollThrowsExceptionForMissingNamedRoll() {
        when(imageGroupRepository.getRollByOwnerAndName(testUser, "non-existent roll")).thenReturn(null);

        imageService.findNamedOrDefaultRoll(TEST_ROLL_NAME, testUser);
    }

    @Test
    public void testAppendImageToGroup() {
        ImageGroup group = new ImageGroup("test", testUser, ImageGroup.Type.ROLL);

        Photo image = new Photo("test", testUser);
        ImageFrame frame = imageService.addImageToGroup(image, group);

        assertEquals(1, frame.getPosition());
        assertEquals(group, frame.getImageGroup());
        assertEquals(image, frame.getImage());
        assertEquals(testUser, frame.getOwner());

        verify(imageGroupRepository).saveGroup(group);
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
