package com.trailmagic.image.ui;

import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupNotFoundException;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.NoSuchImageGroupException;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.web.util.WebRequestTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.SortedSet;

@Controller
public class ImageGroupDisplayController {
    private UserRepository userRepository;
    private ImageGroupRepository imageGroupRepository;
    private ImageSecurityService imageSecurityService;
    private WebRequestTools webRequestTools;

    private static final String IMG_GROUP_VIEW = "imageGroup";
    private static Logger log =
        LoggerFactory.getLogger(ImageGroupDisplayController.class);

    @Autowired
    public ImageGroupDisplayController(ImageGroupRepository imageGroupRepository,
                                       ImageSecurityService imageSecurityService,
                                       UserRepository userRepository,
                                       WebRequestTools webRequestTools) {
        super();
        this.imageGroupRepository = imageGroupRepository;
        this.imageSecurityService = imageSecurityService;
        this.userRepository = userRepository;
        this.webRequestTools = webRequestTools;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ImageGroup.Type.class, new ImageGroupTypeUrlComponentPropertyEditor());
    }

    @RequestMapping("/{groupType}/{screenName}/{groupName}")
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
        log.debug("Frames contains " + frames.size() + " items.");
        model.addAttribute("frames", frames);

        return new ModelAndView(IMG_GROUP_VIEW, model);
    }
}
