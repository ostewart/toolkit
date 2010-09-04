package com.trailmagic.resizer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

/**
 * Created by: oliver on Date: Aug 1, 2010 Time: 8:24:58 PM
 */
@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class ImageMagickImageResizerTest {
    private ImageResizer resizer;
    private File srcFile;
    @Mock CommandExcecutor executor;

    public static final String PORTRAIT_RESULT_IDENTIFY = "/tmp/portrait.jpg[1] JPEG 128x171 128x171+0+0 8-bit DirectClass 11.1KB 0.000u 0:00.000";
    private static final String PORTRAIT_COMMAND = "convert -quality 80 -resize";
    private static final String LANDSCAPE_COMMAND = "convert -quality 80 -resize";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        resizer = new ImageMagickImageResizer(executor);
        srcFile = new File(ClassLoader.getSystemResource("TEST_PORTRAIT.JPG").toURI());
    }

    @Test
    public void testResizePortraitImage() throws Exception {
        File file = resizer.resizeImage(srcFile, new ImageFileInfo(192, 128, "image/jpeg"), 128);

        verify(executor, times(1)).exec(startsWith(PORTRAIT_COMMAND), eq("128x>"), anyString(), anyString());
        assertNotNull(file);
        file.delete();
    }

    @Test
    public void testResizeLandscapeImage() throws Exception {
        File file = resizer.resizeImage(srcFile, new ImageFileInfo(128, 192, "image/jpeg"), 128);

        verify(executor, times(1)).exec(startsWith(LANDSCAPE_COMMAND), eq("x128>"), anyString(), anyString());
        assertNotNull(file);
        file.delete();
    }

    @Test
    public void testIdentify() throws IOException {
        File file = File.createTempFile("testIdentify", "jpg");
        when(executor.exec("identify " + file.getAbsolutePath())).thenReturn(Arrays.asList(PORTRAIT_RESULT_IDENTIFY));

        ImageFileInfo info = resizer.identify(file);

        assertEquals(128, info.getHeight());
        assertEquals(171, info.getWidth());
        assertEquals("image/jpeg", info.getFormat());
        file.delete();
    }

    @Test
    public void testResizeSmallPortraitImage() {
    }

    @Test
    public void testResizeSmallLandscapeImage() {
    }

    @Test
    public void testFileNameWithSpaces() {
    }
    // given input image, what size images should we generate?
}
