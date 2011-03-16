package com.trailmagic.image.ui;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.Photo;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.web.util.WebRequestTools;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ImageDisplayControllerTest {
    public static final long IMAGE_ID = 1234L;
    @Mock private WebRequestTools webRequestTools;
    @Mock private ImageSecurityService imageSecurityService;
    @Mock private ImageGroupRepository imageGroupRepository;
    @Mock private ImageRepository imageRepository;
    private ImageDisplayController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new ImageDisplayController(imageRepository, imageGroupRepository, imageSecurityService, webRequestTools);
    }

    @Test
    public void testFormBackingObjectReturnsImage() throws Exception {
        final Photo photo = new Photo("test", new User("tester"));
        photo.setId(IMAGE_ID);
        when(imageRepository.getById(IMAGE_ID)).thenReturn(photo);

        final Image result = controller.formBackingObject(new MockHttpServletRequest(), IMAGE_ID);

        assertEquals(photo, result);
    }
}
