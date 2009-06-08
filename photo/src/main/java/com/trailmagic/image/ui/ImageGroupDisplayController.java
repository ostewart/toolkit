package com.trailmagic.image.ui;

import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.NoSuchImageGroupException;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.web.util.ImageRequestInfo;
import com.trailmagic.web.util.MalformedUrlException;
import com.trailmagic.web.util.WebRequestTools;
import java.util.SortedSet;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ImageGroupDisplayController {
    private UserRepository userRepository;
    private ImageGroupRepository imageGroupRepository;
    private ImageSecurityService imageSecurityService;
    private WebRequestTools webRequestTools;
    
    private static final String IMG_GROUP_VIEW = "imageGroup";
    private static Logger log =
        Logger.getLogger(ImageGroupDisplayController.class);

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

    public ModelAndView handleDisplayGroup(HttpServletRequest request,
                                           ModelMap model)
            throws NoSuchImageGroupException, MalformedUrlException {
        
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        User owner = userRepository.getByScreenName(iri.getScreenName());
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
}
