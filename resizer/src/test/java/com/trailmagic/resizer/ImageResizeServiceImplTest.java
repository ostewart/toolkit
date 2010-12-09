package com.trailmagic.resizer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by: oliver on Date: Sep 4, 2010 Time: 1:44:25 AM
 */
@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class ImageResizeServiceImplTest {
    @Mock
    private ImageResizer imageResizer;
    private ImageResizeService imageResizeService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        imageResizeService = new ImageResizeServiceImpl(imageResizer);
    }

    @Test
    public void testScheduleResize() throws Exception {
        ImageFileInfo srcFileInfo = new ImageFileInfo(2050, 3066, "image/jpeg");
        mockIdentifyToReturnSrcFileInfoAndSizes(srcFileInfo, 128, 256, 512, 1024, 2048);
        File srcFile = mockResizerToWriteAndResize();


        List<ImageFileInfo> files = imageResizeService.scheduleResize(new ByteArrayInputStream(new byte[]{}));

        assertResizeCalledWithSizes(srcFileInfo, 128, 256, 512, 1024, 2048);
        
        assertEquals(5, files.size());
        for (ImageFileInfo file : files) {
            assertNotNull(file.getFile());
        }
        verify(srcFile).delete();
    }

    private File mockResizerToWriteAndResize() throws IOException {
        File srcFile = Mockito.mock(File.class);
        when(imageResizer.writeToTempFile(Mockito.<InputStream>any())).thenReturn(srcFile);
        File resultFile = Mockito.mock(File.class);
        when(imageResizer.resizeImage(Mockito.<File>any(), Mockito.<ImageFileInfo>any(), Mockito.anyInt()))
                .thenReturn(resultFile);
        return srcFile;
    }

    private void mockIdentifyToReturnSrcFileInfoAndSizes(ImageFileInfo srcFileInfo, Integer... sizes) {
        List<ImageFileInfo> infos = new ArrayList<ImageFileInfo>();
        for (Integer size : sizes) {
            infos.add(new ImageFileInfo(size, size * 2, "image/jpeg"));
        }
        when(imageResizer.identify(Mockito.<File>any())).thenReturn(srcFileInfo,
                                                                    infos.toArray(new ImageFileInfo[infos.size()]));
    }


    @Test public void testOnlyCreatesSmallerSizeImages() throws IOException {
        ImageFileInfo srcFileInfo = new ImageFileInfo(200, 400, "image/jpeg");
        mockIdentifyToReturnSrcFileInfoAndSizes(srcFileInfo, 128);
        File srcFile = mockResizerToWriteAndResize();


        List<ImageFileInfo> files = imageResizeService.scheduleResize(new ByteArrayInputStream(new byte[]{}));

        assertResizeCalledWithSizes(srcFileInfo, 128);

        assertEquals(1, files.size());
        for (ImageFileInfo file : files) {
            assertNotNull(file.getFile());
        }
        verify(srcFile).delete();

    }

    @Test public void testOnlyCreatesEqualAndSmallerSizeImages() throws IOException {
        ImageFileInfo srcFileInfo = new ImageFileInfo(256, 400, "image/jpeg");
        mockIdentifyToReturnSrcFileInfoAndSizes(srcFileInfo, 128, 256);
        File srcFile = mockResizerToWriteAndResize();


        List<ImageFileInfo> files = imageResizeService.scheduleResize(new ByteArrayInputStream(new byte[]{}));

        assertResizeCalledWithSizes(srcFileInfo, 128, 256);

        assertEquals(2, files.size());
        for (ImageFileInfo file : files) {
            assertNotNull(file.getFile());
        }
        verify(srcFile).delete();

    }

    private void assertResizeCalledWithSizes(ImageFileInfo srcFileInfo, Integer... sizes) {
        for (Integer size : sizes) {
            verify(imageResizer, Mockito.times(1)).resizeImage(Mockito.<File>any(), eq(srcFileInfo), eq(size));
        }
        verify(imageResizer, Mockito.times(sizes.length)).resizeImage(Mockito.<File>any(), eq(srcFileInfo), anyInt());
    }
}
