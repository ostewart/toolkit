package com.trailmagic.resizer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by: oliver on Date: Sep 4, 2010 Time: 1:44:25 AM
 */
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
        ImageFileInfo imageFileInfo = new ImageFileInfo();
        when(imageResizer.identify(Mockito.<File>any())).thenReturn(imageFileInfo);

        List<File> files = imageResizeService.scheduleResize(1234L, 4567L);

        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(imageFileInfo), eq(128));
        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(imageFileInfo), eq(256));
        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(imageFileInfo), eq(512));
        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(imageFileInfo), eq(1024));
        verify(imageResizer).resizeImage(Mockito.<File>any(), eq(imageFileInfo), eq(2048));

        assertEquals(5, files.size());
    }
}
