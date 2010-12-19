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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class ImageDisplayController {
    private ImageRepository imageRepository;
    private ImageGroupRepository imageGroupRepository;
    private ImageSecurityService imageSecurityService;
    private WebRequestTools webRequestTools;

    @Autowired
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

    @InitBinder
    protected void initBinder(WebDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, "captureDate",
                                    new CustomDateEditor(SimpleDateFormat.getDateInstance(DateFormat.SHORT), true));
        binder.registerCustomEditor(ImageGroup.Type.class, new ImageGroupTypeUrlComponentPropertyEditor());
    }

    @ModelAttribute("image")
    public Image formBackingObject(HttpServletRequest request) throws Exception {
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

    @RequestMapping("/{groupType}/{screenName}/{groupName}/{imageId}")
    public ModelAndView showForm(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @ModelAttribute("image") Image image, BindingResult errors,
                                 @PathVariable("groupType") ImageGroup.Type groupType,
                                 @PathVariable("groupName") String groupName,
                                 @PathVariable("imageId") Long imageId) throws Exception {
        if (webRequestTools.preHandlingFails(request, response, false)) return null;

        @SuppressWarnings("unchecked")
        Map<String, Object> model = errors.getModel();
        model.put("isEditView", errors.hasErrors() || isEditMode(request));
        return setupModel(groupName, groupType, imageId, model);
    }

    @RequestMapping(value = "/{groupType}/{screenName}/{groupName}/{imageId}", method = RequestMethod.POST)
    protected ModelAndView onSubmit(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @ModelAttribute("image") Image image, BindingResult errors,
                                    @PathVariable("groupType") ImageGroup.Type groupType,
                                    @PathVariable("groupName") String groupName,
                                    @PathVariable("imageId") Long imageId,
                                    ModelMap model) throws Exception {
        if (webRequestTools.preHandlingFails(request, response, false)) return null;


        if (errors.hasErrors()) {
            model.put("isEditView", true);
            return setupModel(groupName, groupType, imageId, model);
        } else {
            imageRepository.save(image);
            return new ModelAndView("redirect:" + imageId);
        }
    }


    private ModelAndView setupModel(String groupName, ImageGroup.Type groupType, long imageId, Map<String, Object> model) throws MalformedUrlException {
        ImageFrame frame = imageGroupRepository.getImageFrameByGroupNameTypeAndImageId(groupName,
                                                                                       groupType,
                                                                                       imageId);
        model.put("frame", frame);
        model.put("image", frame.getImage());
        model.put("group", frame.getImageGroup());
        model.put("imageIsPublic", imageSecurityService.isPublic(frame.getImage()));
        model.put("groupsContainingImage", findGroupsContainingImage(frame));
        model.put("prevFrame", frame.previous());
        model.put("nextFrame", frame.next());


        // got user, group, and frame number: show that frame
        return new ModelAndView("imageDisplay", model);
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
