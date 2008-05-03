package com.trailmagic.image.ui;

import com.trailmagic.image.ImageFactory;
import junit.framework.TestCase;
import org.easymock.EasyMock;

public class ImageEditControllerTests extends TestCase {
    private ImageEditController controller;

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

        ImageFactory imageFactory = EasyMock.createMock(ImageFactory.class);
        controller = new ImageEditController(imageFactory);
    }

    public void testEditImage() {

    }

}
