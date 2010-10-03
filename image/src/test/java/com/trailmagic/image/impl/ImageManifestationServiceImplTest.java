package com.trailmagic.image.impl;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.ImageManifestationService;
import com.trailmagic.image.Photo;
import com.trailmagic.image.security.SecurityTestHelper;
import com.trailmagic.resizer.ImageFileInfo;
import com.trailmagic.resizer.ImageResizeService;
import com.trailmagic.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Blob;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by: oliver on Date: Oct 2, 2010 Time: 5:28:42 PM
 */
public class ImageManifestationServiceImplTest {
    private ImageManifestationService imageManifestationService;
    @Mock private ImageResizeService imageResizeService;
    @Mock private HibernateUtil hibernateUtil;
    @Mock private ImageInitializer imageInitializer;

    private static final int PORTRAIT_WIDTH = 1935;
    private static final int PORTRAIT_HEIGHT = 2592;
    private static final ByteArrayInputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[]{});


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        new SecurityTestHelper().disableSecurityInterceptor();

        imageManifestationService = new ImageManifestationServiceImpl(imageResizeService, hibernateUtil, imageInitializer);

    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test
    public void testCreateManifestation() throws Exception {
        File resizeTempFile = mock(File.class);
        when(resizeTempFile.delete()).thenReturn(true);
        ImageFileInfo fileInfo = testFileInfo(resizeTempFile, 10, 10);

        File srcFile = mock(File.class);
        when(srcFile.delete()).thenReturn(true);

        when(imageResizeService.writeFile(Mockito.<InputStream>any())).thenReturn(srcFile);
        when(imageResizeService.identify(srcFile)).thenReturn(testFileInfo(srcFile, PORTRAIT_WIDTH, PORTRAIT_HEIGHT));
        Blob srcBlob = mock(Blob.class);
        when(hibernateUtil.toBlob(srcFile)).thenReturn(srcBlob);
        when(imageResizeService.scheduleResize(srcFile)).thenReturn(Arrays.asList(fileInfo));

        Photo photo = new Photo();
        photo.setOwner(new User("tester"));
        imageManifestationService.createManifestationsFromOriginal(photo, EMPTY_INPUT_STREAM);

        assertEquals(2, photo.getManifestations().size());
        // this one's bigger, so first
        HeavyImageManifestation original = (HeavyImageManifestation) photo.getManifestations().last();
        assertEquals("image/jpeg", original.getFormat());
        assertEquals(photo, original.getImage());
        assertEquals(srcBlob, original.getData());
        assertEquals(PORTRAIT_WIDTH, original.getWidth());
        assertEquals(PORTRAIT_HEIGHT, original.getHeight());

        verify(imageInitializer).saveNewImageManifestation(original, false);
        verify(imageResizeService).scheduleResize(srcFile);
        verify(imageInitializer).saveNewImageManifestation((HeavyImageManifestation) photo.getManifestations().first(), false);
        verify(srcFile).delete();
        verify(resizeTempFile).delete();
    }

    private ImageFileInfo testFileInfo(File file, int width, int height) {
        ImageFileInfo fileInfo = new ImageFileInfo(width, height, "image/jpeg");
        fileInfo.setFile(file);
        return fileInfo;
    }

}
