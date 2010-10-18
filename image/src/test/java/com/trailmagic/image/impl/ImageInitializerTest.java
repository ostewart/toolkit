package com.trailmagic.image.impl;

import com.trailmagic.image.*;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.image.security.SecurityTestHelper;
import com.trailmagic.user.User;
import com.trailmagic.util.SecurityUtil;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by: oliver on Date: Jun 25, 2010 Time: 9:40:33 PM
 */
public class ImageInitializerTest {
    private ImageInitializer imageInitializer;
    private User testUser;
    @Mock private ImageSecurityService imageSecurityService;
    @Mock private ImageGroupRepository imageGroupRepository;
    @Mock private ImageRepository imageRepository;
    @Mock private SecurityUtil securityUtil;
    @Mock private ImageManifestationRepository imageManifestationRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        new SecurityTestHelper().disableSecurityInterceptor();

        imageInitializer = new ImageInitializer(imageGroupRepository, imageRepository, imageSecurityService, imageManifestationRepository, securityUtil);
        testUser = new User("testy");

    }

    @Test
    public void testSaveNewImage() throws Exception {
        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        Image image = makePhoto("test", testUser);
        imageInitializer.saveNewImage(image);
        Mockito.verify(imageSecurityService).addOwnerAcl(image);
    }

    @Test
    public void testSaveNewImageBlowsUpForExistingWithSameName() {
        
    }

    @Test
    public void testSaveNewImageOverridesOwner() {
        Photo newPhoto = makePhoto("test", null);

        when(securityUtil.getCurrentUser()).thenReturn(testUser);

        imageInitializer.saveNewImage(newPhoto);

        Assert.assertEquals(testUser, newPhoto.getOwner());
        Mockito.verify(imageRepository).saveNew(newPhoto);
        Mockito.verify(imageSecurityService).addOwnerAcl(newPhoto);
    }

    @Test(expected = IllegalStateException.class)
    public void testCantCreateImageWithoutUser() {
        when(securityUtil.getCurrentUser()).thenReturn(null);
        
        imageInitializer.saveNewImage(makePhoto("test", null));
    }

    @Test(expected = IllegalStateException.class)
    public void testCantCreateImageGroupWithoutUser() {
        when(securityUtil.getCurrentUser()).thenReturn(null);

        imageInitializer.saveNewImageGroup(makeImageGroup(null));
    }


    private Photo makePhoto(String name, User owner) {
        return new Photo(name, owner);
    }

    @Test
    public void testSaveNewImageGroup() {
        ImageGroup group = makeImageGroup(testUser);

        imageInitializer.saveNewImageGroup(group);

        verify(imageGroupRepository).saveNewGroup(group);
        verify(imageSecurityService).addOwnerAcl(group);
    }

    @Test
    public void testSaveNewImageGroupSetsPreviewImage() {
        ImageGroup group = makeImageGroup(testUser);
        Photo expectedPreviewImage = makePhoto("test", testUser);
        group.addFrame(makeFrame(expectedPreviewImage, 1));
        group.addFrame(makeFrame(makePhoto("test", testUser), 2));

        imageInitializer.saveNewImageGroup(group);

        assertEquals(expectedPreviewImage, group.getPreviewImage());

        verify(imageGroupRepository).saveNewGroup(group);
        verify(imageSecurityService).addOwnerAcl(group);
    }

    @Test
    public void testSaveNewImageGroupSetsPreviewImageWithNoFrames() {
        ImageGroup group = makeImageGroup(testUser);

        imageInitializer.saveNewImageGroup(group);

        assertNull(group.getPreviewImage());

        verify(imageGroupRepository).saveNewGroup(group);
        verify(imageSecurityService).addOwnerAcl(group);
    }

    private ImageFrame makeFrame(Photo photo, int pos) {
        ImageFrame frame = new ImageFrame(photo);
        frame.setPosition(pos);
        return frame;
    }

    @Test
    public void testSaveNewImageManifestation() throws Exception {
        HeavyImageManifestation mf = new HeavyImageManifestation();
        mf.setData(Hibernate.createBlob(new byte[0]));

        imageInitializer.saveNewImageManifestation(mf);

        verify(imageManifestationRepository).saveNewImageManifestation(mf);
        verify(imageManifestationRepository).cleanFromSession(mf);
    }

    @Test
    public void testSaveNewImageManifestationWithoutClear() throws Exception {
        HeavyImageManifestation mf = new HeavyImageManifestation();
        mf.setData(Hibernate.createBlob(new byte[0]));

        imageInitializer.saveNewImageManifestation(mf, false);

        verify(imageManifestationRepository).saveNewImageManifestation(mf);
        verify(imageManifestationRepository, never()).cleanFromSession(mf);
    }

    @Test
    public void testSaveNewImageGroupOverridesOwner() {
        ImageGroup group = makeImageGroup(null);

        when(securityUtil.getCurrentUser()).thenReturn(testUser);

        imageInitializer.saveNewImageGroup(group);

        Assert.assertEquals(testUser, group.getOwner());
        Mockito.verify(imageGroupRepository).saveNewGroup(group);
        Mockito.verify(imageSecurityService).addOwnerAcl(group);
    }

    private ImageGroup makeImageGroup(User owner) {
        return new ImageGroup("test", owner, ImageGroup.Type.ROLL);
    }

}
