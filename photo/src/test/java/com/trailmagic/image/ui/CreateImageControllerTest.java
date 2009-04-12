package com.trailmagic.image.ui;

import junit.framework.TestCase;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.WebDataBinder;
import org.apache.commons.lang.ArrayUtils;
import com.trailmagic.image.ImageService;
import com.trailmagic.image.ImageMetadata;
import com.trailmagic.image.Photo;
import java.io.IOException;
import java.io.InputStream;

public class CreateImageControllerTest extends TestCase {
    private static final long TEST_PHOTO_ID = 12234L;
    private ImageService imageService;
    private CreateImageController controller;

    protected void setUp() throws Exception {
        super.setUp();
        imageService = Mockito.mock(ImageService.class);
        controller = new CreateImageController(imageService);
    }

    public void testSetsStatusAndOutputsAnId() throws IOException {
        final Photo photo = new Photo();
        photo.setId(TEST_PHOTO_ID);
        Mockito.when(imageService.createImage(Mockito.any(ImageMetadata.class), Mockito.any(InputStream.class), Mockito.any(String.class))).thenReturn(photo);

        final ImageUpload imageUpload = new ImageUpload();
        imageUpload.setFile(new MockMultipartFile("name", "filename", "image/jpeg", "image data".getBytes()));
        final MockHttpServletResponse response = new MockHttpServletResponse();
        response.setWriterAccessAllowed(true);
        controller.processNewImage(imageUpload, response);
        assertEquals(201, response.getStatus());
        assertEquals(Long.valueOf(TEST_PHOTO_ID).toString(), response.getContentAsString().trim());
    }

    public void testSetsRequiredFields() {
        final WebDataBinder binder = new WebDataBinder(new Photo());
        controller.setupBinder(binder);

        assertTrue(ArrayUtils.contains(binder.getRequiredFields(), "file"));
    }
}
