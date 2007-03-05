package com.trailmagic.image.ui;

import com.trailmagic.image.ImageFactory;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ImageEditController extends SimpleFormController {

    private ImageFactory imageFactory;

    public ImageEditController(ImageFactory imageFactory) {
        super();
        this.imageFactory = imageFactory;
    }

// Model Requirements:
// user: currently logged in user
// image: Image to display

// Optional Attributes:
// imageGroup: ImageGroup
// frame: current ImageFrame
// nextFrame: ImageFrame
// prevFrame: ImageFrame
// nextImage: Image
// prevImage: Image
// groupsContainingImage: ImageGroups that contain frame.image


    @Override
    protected Object formBackingObject(HttpServletRequest req) throws Exception {
        Long imageId = WebSupport.extractImageIdFromRequest(req);
        return imageFactory.getById(imageId);
    }

    @Override
    protected Map referenceData(HttpServletRequest req,
                                Object arg1,
                                Errors arg2) throws Exception {
        Map<String, Object> refData = new HashMap<String, Object>();

        Long imageId = WebSupport.extractImageIdFromRequest(req);
        refData.put("image", imageFactory.getById(imageId));

        return refData;

    }

}
