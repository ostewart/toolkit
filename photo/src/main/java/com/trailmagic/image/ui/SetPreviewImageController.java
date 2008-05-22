package com.trailmagic.image.ui;

import com.trailmagic.image.ImageService;
import java.net.URI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SetPreviewImageController {
    ImageService imageService;
    
    public SetPreviewImageController(ImageService imageService) {
        super();
        this.imageService = imageService;
    }

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView setImageGroupPreview(@RequestParam(required=true) long imageGroupId,
                                             @RequestParam(required=true) long imageId,
                                             @RequestParam(required=true) URI returnToUrl) {
        imageService.setImageGroupPreview(imageGroupId, imageId);
        return new ModelAndView("redirect:" + returnToUrl);
    }
}
