package com.trailmagic.image.impl;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.ImageManifestationService;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.Photo;
import com.trailmagic.image.security.SecurityTestHelper;
import com.trailmagic.resizer.ImageFileInfo;
import com.trailmagic.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.sql.Blob;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by: oliver on Date: Oct 2, 2010 Time: 5:28:42 PM
 */
public class ImageManifestationServiceImplTest {
    private ImageManifestationService imageManifestationService;
    @Mock private HibernateUtil hibernateUtil;
    @Mock private ImageRepository imageRepository;

    @Mock private ImageInitializer imageInitializer;
    private static final int PORTRAIT_WIDTH = 1935;
    private static final int PORTRAIT_HEIGHT = 2592;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        new SecurityTestHelper().disableSecurityInterceptor();

        imageManifestationService = new ImageManifestationServiceImpl(hibernateUtil, imageInitializer, imageRepository);

    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test
    public void testCreateManifestation() throws Exception {
        ImageFileInfo fileInfo = testFileInfo(null, PORTRAIT_WIDTH, PORTRAIT_HEIGHT);

        Blob srcBlob = mock(Blob.class);
        when(hibernateUtil.toBlob(fileInfo.getFile())).thenReturn(srcBlob);

        Photo photo = new Photo();
        photo.setOwner(new User("tester"));

        when(imageRepository.getById(photo.getId())).thenReturn(photo);

        imageManifestationService.createManifestation(photo, fileInfo, true);

        assertEquals(1, photo.getManifestations().size());
        HeavyImageManifestation original = (HeavyImageManifestation) photo.getManifestations().last();
        assertEquals("image/jpeg", original.getFormat());
        assertEquals(photo, original.getImage());
        assertEquals(srcBlob, original.getData());
        assertEquals(PORTRAIT_WIDTH, original.getWidth());
        assertEquals(PORTRAIT_HEIGHT, original.getHeight());

        verify(imageInitializer).saveNewImageManifestation(original, true);
    }

    private ImageFileInfo testFileInfo(File file, int width, int height) {
        ImageFileInfo fileInfo = new ImageFileInfo(width, height, "image/jpeg");
        fileInfo.setFile(file);
        return fileInfo;
    }

}
