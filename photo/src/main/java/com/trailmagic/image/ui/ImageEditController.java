package com.trailmagic.image.ui;

import com.trailmagic.image.ImageRepository;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ImageEditController extends SimpleFormController {

    private ImageRepository imageRepository;

    public ImageEditController(ImageRepository imageFactory) {
        super();
        this.imageRepository = imageFactory;
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
        return imageRepository.getById(imageId);
    }

    @Override
    protected Map<String,Object> referenceData(HttpServletRequest req,
                                               Object arg1,
                                               Errors arg2) throws Exception {
        Map<String, Object> refData = new HashMap<String, Object>();

        Long imageId = WebSupport.extractImageIdFromRequest(req);
        refData.put("image", imageRepository.getById(imageId));

        return refData;

    }

}
