package com.trailmagic.resizer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        ImageFileInfo srcFileInfo = new ImageFileInfo();
        when(imageResizer.identify(Mockito.<File>any())).thenReturn(srcFileInfo,
                                                                    new ImageFileInfo(),
                                                                    new ImageFileInfo(),
                                                                    new ImageFileInfo(),
                                                                    new ImageFileInfo(),
                                                                    new ImageFileInfo(),
                                                                    new ImageFileInfo());
        File srcFile = Mockito.mock(File.class);
        when(imageResizer.writeToTempFile(Mockito.<InputStream>any())).thenReturn(srcFile);
        File resultFile = File.createTempFile("ImageResizeServiceImplTest", "jpg");
        when(imageResizer.resizeImage(Mockito.<File>any(), Mockito.<ImageFileInfo>any(), Mockito.anyInt()))
                .thenReturn(resultFile);


        List<ImageFileInfo> files = imageResizeService.scheduleResize(new ByteArrayInputStream(new byte[]{}));

        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(srcFileInfo), eq(128));
        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(srcFileInfo), eq(256));
        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(srcFileInfo), eq(512));
        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(srcFileInfo), eq(1024));
        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(srcFileInfo), eq(2048));

        assertEquals(5, files.size());
        for (ImageFileInfo file : files) {
            assertNotNull(file.getFile());
        }
        resultFile.delete();
        verify(srcFile).delete();
    }
}
