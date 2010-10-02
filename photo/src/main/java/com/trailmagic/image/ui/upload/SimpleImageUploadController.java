package com.trailmagic.image.ui.upload;

import com.trailmagic.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import sun.swing.CachedPainter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

@Controller
@RequestMapping("/upload/")
public class SimpleImageUploadController {
    private ImageService imageService;

    @Autowired
    public SimpleImageUploadController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping("{groupId}")
    public ModelAndView showForm(@PathVariable("groupId") Long groupId) {
        return new ModelAndView("groupUpload", new HashMap<String, Object>());
    }

    @RequestMapping(value = "{groupId}", method = RequestMethod.POST)
    public String handleUpload(@PathVariable("groupId") Long groupId, HttpServletRequest req) throws IOException {
        imageService.createImage(req.getInputStream());

        return "redirect:";
    }
}
