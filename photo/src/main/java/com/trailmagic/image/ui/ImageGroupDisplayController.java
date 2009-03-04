package com.trailmagic.image.ui;

import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.NoSuchImageGroupException;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import com.trailmagic.web.util.ImageRequestInfo;
import com.trailmagic.web.util.MalformedUrlException;
import com.trailmagic.web.util.WebRequestTools;
import java.util.SortedSet;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ImageGroupDisplayController {
    private UserFactory userFactory;
    private ImageGroupRepository imageGroupRepository;
    private ImageSecurityService imageSecurityService;
    private WebRequestTools webRequestTools;
    
    private static final String IMG_GROUP_VIEW = "imageGroup";
    private static Logger log =
        Logger.getLogger(ImageGroupDisplayController.class);


    public ImageGroupDisplayController(ImageGroupRepository imageGroupRepository,
                                       ImageSecurityService imageSecurityService,
                                       UserFactory userFactory,
                                       WebRequestTools webRequestTools) {
        super();
        this.imageGroupRepository = imageGroupRepository;
        this.imageSecurityService = imageSecurityService;
        this.userFactory = userFactory;
        this.webRequestTools = webRequestTools;
    }

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView handleDisplayGroup(HttpServletRequest request,
                                           ModelMap model)
            throws NoSuchImageGroupException, MalformedUrlException {
        
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        User owner = userFactory.getByScreenName(iri.getScreenName());
        ImageGroup group =
            imageGroupRepository.getByOwnerNameAndTypeWithFrames(owner,
                                                                 iri.getImageGroupName(),
                                                                 iri.getImageGroupType());
        model.addAttribute("imageGroup", group);
        model.addAttribute("imageGroupIsPublic",
                           imageSecurityService.isPublic(group));

        SortedSet<ImageFrame> frames = group.getFrames();
        log.debug("Frames contains " + frames.size() + " items.");
        model.addAttribute("frames", frames);

        return new ModelAndView(IMG_GROUP_VIEW, model);
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public void processSubmit(@ModelAttribute("imageGroup") ImageGroup imageGroup) {
        
    }
}
