package com.trailmagic.image.ui;

import com.trailmagic.image.*;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.web.util.WebRequestTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

@Controller
public class ImageGroupDisplayController {
    private UserRepository userRepository;
    private ImageGroupRepository imageGroupRepository;
    private ImageSecurityService imageSecurityService;
    private WebRequestTools webRequestTools;
    private ImageService imageService;

    @Autowired
    public ImageGroupDisplayController(ImageGroupRepository imageGroupRepository,
                                       ImageSecurityService imageSecurityService,
                                       UserRepository userRepository,
                                       WebRequestTools webRequestTools, ImageService imageService) {
        super();
        this.imageGroupRepository = imageGroupRepository;
        this.imageSecurityService = imageSecurityService;
        this.userRepository = userRepository;
        this.webRequestTools = webRequestTools;
        this.imageService = imageService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ImageGroup.Type.class, new ImageGroupTypeUrlComponentPropertyEditor());
    }

    @RequestMapping(value="/{groupType}/{screenName}/{groupName}", method = RequestMethod.GET)
    public ModelAndView handleDisplayGroup(HttpServletRequest request, HttpServletResponse response,
                                           @PathVariable("groupType") ImageGroup.Type groupType,
                                           @PathVariable("screenName") String screenName,
                                           @PathVariable("groupName") String groupName,
                                           ModelMap model)
            throws NoSuchImageGroupException, IOException {

        if (webRequestTools.preHandlingFails(request, response, true)) return null;

        User owner = userRepository.getByScreenName(screenName);
        ImageGroup group = imageGroupRepository.getByOwnerNameAndTypeWithFrames(owner, groupName, groupType);
        if (group == null) {
            throw new ImageGroupNotFoundException(groupType.getDisplayString() + " not found: " + groupName);
        }
        model.addAttribute("imageGroup", group);
        model.addAttribute("imageGroupIsPublic", imageSecurityService.isPublic(group));

        SortedSet<ImageFrame> frames = group.getFrames();
        model.addAttribute("frames", frames);

        if (request.getParameter("createRoll") != null) {
            return new ModelAndView("newGroup");
        }

        return new ModelAndView("imageGroup", model);
    }

    @RequestMapping(value="/{groupType}/{screenName}/{groupName}", method = RequestMethod.POST)
    public ModelAndView handleCreateNewGroupWithImages(@PathVariable("screenName") String screenName,
                                                       @RequestParam("rollName") String rollName,
                                                       @RequestParam("selectedFrames") HashSet<Long> selectedFrameIds) {
        imageService.createRollWithFrames(rollName, selectedFrameIds);
        return new ModelAndView("redirect:/rolls/" + screenName + "/" + rollName);
    }
}
