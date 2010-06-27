package com.trailmagic.image.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import com.trailmagic.image.ImageMetadata;
import com.trailmagic.image.ImageService;
import com.trailmagic.image.Image;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/image")
public class CreateImageController {
    private ImageService imageService;

    @Autowired
    public CreateImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @InitBinder
    public void setupBinder(WebDataBinder binder) {
        binder.setRequiredFields("file");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String displayForm() {
        return "addImage";
    }

    @ModelAttribute("imageUpload")
    public ImageUpload formBackingObject() {
        return new ImageUpload();
    }

    @RequestMapping(method= RequestMethod.POST)
    public void processNewImage(@ModelAttribute ImageUpload imageUpload, HttpServletResponse response) throws IOException {
        ImageMetadata imageMetadata = new ImageMetadata();
        final MultipartFile multipartFile = imageUpload.getFile();
        imageMetadata.setShortName(multipartFile.getOriginalFilename());
        imageMetadata.setCaption(imageUpload.getCaption());
        imageMetadata.setCopyright(imageUpload.getCopyright());
        imageMetadata.setCreator(imageUpload.getCreator());
        imageMetadata.setDisplayName(imageUpload.getTitle());
        imageMetadata.setRollName(imageUpload.getRollName());

        Image image = imageService.createImage(imageMetadata, multipartFile.getInputStream(), multipartFile.getContentType());

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().println(image.getId());
    }
}
