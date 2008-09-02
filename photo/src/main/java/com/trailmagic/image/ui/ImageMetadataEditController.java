package com.trailmagic.image.ui;

import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;

import org.springframework.beans.propertyeditors.CustomDateEditor;

import java.util.Date;

import org.springframework.web.bind.ServletRequestDataBinder;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageRepository;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ImageMetadataEditController extends SimpleFormController {
    private ImageRepository imageRepository;
    
    public ImageMetadataEditController(ImageRepository imageRepository) {
        super();
        this.imageRepository = imageRepository;
    }

    @Override
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        
        binder.registerCustomEditor(Date.class, "captureDate",
                                    new CustomDateEditor(SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT), true));
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        
        Long imageId = ServletRequestUtils.getRequiredLongParameter(request, "imageId");
        Image image = imageRepository.getById(imageId);
        if (image == null) {
            throw new Exception("no such image");
        }
        return image;
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest request,
                                    HttpServletResponse response,
                                    BindException errors) throws Exception {
        return new ModelAndView(getRedirectView(request) + "?mode=edit",
                                errors.getModel());
    }
    
    private String getRedirectView(HttpServletRequest request) throws Exception {
        return "redirect:"
            + ServletRequestUtils.getRequiredStringParameter(request, "redirectUri");
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Object command, BindException errors)
            throws Exception {
        
        Image image = (Image) command;
        
        imageRepository.save(image);
        ModelAndView mav = new ModelAndView(getRedirectView(request), "image", image);
        return mav;
    }
}
