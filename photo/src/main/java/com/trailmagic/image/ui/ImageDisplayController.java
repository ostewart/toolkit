package com.trailmagic.image.ui;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.web.util.ImageRequestInfo;
import com.trailmagic.web.util.MalformedUrlException;
import com.trailmagic.web.util.WebRequestTools;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ImageDisplayController extends SimpleFormController {
    private ImageRepository imageRepository;
    private ImageGroupRepository imageGroupRepository;
    private ImageSecurityService imageSecurityService;
    private WebRequestTools webRequestTools;

    public ImageDisplayController(ImageRepository imageRepository,
                                  ImageGroupRepository imageGroupRepository,
                                  ImageSecurityService imageSecurityService,
                                  WebRequestTools webRequestTools) {
        super();
        this.imageRepository = imageRepository;
        this.imageGroupRepository = imageGroupRepository;
        this.imageSecurityService = imageSecurityService;
        this.webRequestTools = webRequestTools;
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);

        binder.registerCustomEditor(Date.class, "captureDate",
                                    new CustomDateEditor(SimpleDateFormat.getDateInstance(DateFormat.SHORT), true));
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        Image image = imageRepository.getById(iri.getImageId());
        if (image == null) {
            throw new Exception("no such image");
        }
        return image;
    }

    private boolean isEditMode(HttpServletRequest request) throws ServletRequestBindingException {
        String mode = ServletRequestUtils.getStringParameter(request, "mode");
        return "edit".equals(mode);
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest request,
                                    HttpServletResponse response,
                                    BindException errors) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> model = errors.getModel();
        model.put("isEditView", errors.hasErrors() || isEditMode(request));
        return setupModel(request, model);
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Object command, BindException errors) throws Exception {

        Image image = (Image) command;

        imageRepository.save(image);

        @SuppressWarnings("unchecked")
        Map<String, Object> model = errors.getModel();
        model.put("isEditView", false);
        return setupModel(request, model);
    }


    private ModelAndView setupModel(HttpServletRequest request, Map<String, Object> model) throws MalformedUrlException {
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        ImageFrame frame = imageGroupRepository.getImageFrameByGroupNameTypeAndImageId(iri.getImageGroupName(),
                                                                                       iri.getImageGroupType(),
                                                                                       iri.getImageId());
        model.put("frame", frame);
        model.put("image", frame.getImage());
        model.put("group", frame.getImageGroup());
        model.put("imageIsPublic", imageSecurityService.isPublic(frame.getImage()));
        model.put("groupsContainingImage", findGroupsContainingImage(frame));
        model.put("prevFrame", frame.previous());
        model.put("nextFrame", frame.next());


        // got user, group, and frame number: show that frame
        return new ModelAndView(getFormView(), model);
    }

    private List<ImageGroup> findGroupsContainingImage(ImageFrame frame) {
        List<ImageGroup> groupsContainingImage = new ArrayList<ImageGroup>();
        for (ImageGroup group : imageGroupRepository.getByImage(frame.getImage())) {
            if (!frame.getImageGroup().equals(group)) {
                groupsContainingImage.add(group);
            }
        }
        return groupsContainingImage;
    }
}
