package com.trailmagic.image.ui.upload;

import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageService;
import com.trailmagic.image.Photo;
import com.trailmagic.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Controller
@RequestMapping("/upload")
public class SimpleImageUploadController {
    private ImageService imageService;
    private SecurityUtil securityUtil;

    @Autowired
    public SimpleImageUploadController(ImageService imageService, SecurityUtil securityUtil) {
        this.imageService = imageService;
        this.securityUtil = securityUtil;
    }

    @RequestMapping
    public ModelAndView showForm() {
        final HashMap<String, Object> model = new HashMap<String, Object>();
        final ImageGroup imageGroup = imageService.findOrCreateDefaultRollForUser(securityUtil.getCurrentUser());
        model.put("imageGroup", imageGroup);
        model.put("nextFramePosition", imageGroup.nextFramePosition());
        return new ModelAndView("groupUpload", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void handleUpload(@RequestParam(value = "pos", required = false) Integer position, HttpServletRequest req, HttpServletResponse res) throws IOException {
        final Photo image;
        if (position != null) {
            image = imageService.createImageAtPosition(req.getInputStream(), position);
        } else {
            image = imageService.createImage(req.getInputStream());
        }


        res.setStatus(303);
        res.addHeader("Location", res.encodeRedirectURL(req.getContextPath() + "/rolls/" + securityUtil.getCurrentUser().getScreenName() + "/uploads/" + image.getId()));
    }
}
