package com.trailmagic.image.impl;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.ImageManifestationRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class ImageResizeClientImplTest {
    @Mock private ImageManifestationService imageManifestationService;
    @Mock private ImageResizeService imageResizeService;
    @Mock private ImageManifestationRepository imageManifestationRepository;
    private ImageResizeClient imageResizeClient;

    private static final ByteArrayInputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[]{});
    private static final int PORTRAIT_WIDTH = 1935;
    private static final int PORTRAIT_HEIGHT = 2592;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        new SecurityTestHelper().disableSecurityInterceptor();

        imageResizeClient = new ImageResizeClientImpl(imageResizeService, imageManifestationService, imageManifestationRepository);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test
    public void testCreatesOriginalManifestation() throws Exception {
        File srcFile = mockFileToBeDeleted();
        Photo photo = new Photo("name", new User("tester"));
        ImageFileInfo testFileInfo = testFileInfo(srcFile, PORTRAIT_WIDTH, PORTRAIT_HEIGHT);

        when(imageResizeService.writeFile(Mockito.<InputStream>any())).thenReturn(srcFile);
        when(imageResizeService.identify(srcFile)).thenReturn(testFileInfo);

        imageResizeClient.createOriginalManifestation(photo, EMPTY_INPUT_STREAM);

        verify(imageManifestationService).createManifestation(photo, testFileInfo, true);
        verify(srcFile).delete();
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test
    public void testCreatesResizedManifestations() throws Exception {
        File srcFile = mockFileToBeDeleted();
        File resizeFile = mockFileToBeDeleted();
        Photo photo = new Photo("name", new User("tester"));
        ImageFileInfo testFileInfo = testFileInfo(resizeFile, PORTRAIT_WIDTH, PORTRAIT_HEIGHT);
        final SecurityContextImpl securityContext = new SecurityContextImpl();
        final HeavyImageManifestation manifestation = testOriginal();

        when(imageManifestationRepository.findOriginalHeavyForImage(photo.getId())).thenReturn(manifestation);
        when(imageResizeService.writeFile(Mockito.<InputStream>any())).thenReturn(srcFile);
        when(imageResizeService.scheduleResize(srcFile)).thenReturn(Arrays.asList(testFileInfo));

        imageResizeClient.createResizedManifestations(photo, securityContext);

        verify(imageManifestationService).createManifestation(photo, testFileInfo, false);
        verify(srcFile).delete();
        verify(resizeFile).delete();
        assertSame(securityContext, SecurityContextHolder.getContext());
    }

    private HeavyImageManifestation testOriginal() throws SQLException {
        final HeavyImageManifestation manifestation = new HeavyImageManifestation();
        final Blob blob = mock(Blob.class);
        when(blob.getBinaryStream()).thenReturn(EMPTY_INPUT_STREAM);
        manifestation.setData(blob);
        return manifestation;
    }

    private File mockFileToBeDeleted() {
        File resizeTempFile = mock(File.class);
        when(resizeTempFile.delete()).thenReturn(true);
        return resizeTempFile;
    }

    private ImageFileInfo testFileInfo(File file, int width, int height) {
        ImageFileInfo fileInfo = new ImageFileInfo(width, height, "image/jpeg");
        fileInfo.setFile(file);
        return fileInfo;
    }

}
