package com.trailmagic.image.ui.upload;

import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageService;
import com.trailmagic.image.Photo;
import com.trailmagic.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        return new ModelAndView("groupUpload", new HashMap<String, Object>());
    }

    @RequestMapping("{groupId}")
    public ModelAndView showFormForGroup(@PathVariable("groupId") Long groupId) {
        final ImageGroup imageGroup = imageService.findOrCreateDefaultRollForUser(securityUtil.getCurrentUser());
        return new ModelAndView("groupUpload", "imageGroup", imageGroup);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void handleUpload(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final Photo image = imageService.createImage(req.getInputStream());


        res.setStatus(303);
        res.addHeader("Location", res.encodeRedirectURL(req.getContextPath() + "/rolls/" + securityUtil.getCurrentUser().getScreenName() + "/uploads/" + image.getId()));
    }
}
