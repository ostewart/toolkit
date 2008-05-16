package com.trailmagic.image.ui;

import com.trailmagic.image.ImageRepository;
import junit.framework.TestCase;
import org.easymock.EasyMock;

public class ImageEditControllerTests extends TestCase {
    private ImageEditController controller;

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

        ImageRepository imageFactory = EasyMock.createMock(ImageRepository.class);
        controller = new ImageEditController(imageFactory);
    }

    public void testEditImage() {

    }

}
