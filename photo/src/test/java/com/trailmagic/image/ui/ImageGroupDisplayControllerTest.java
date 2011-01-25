package com.trailmagic.image.ui;

import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.Photo;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.web.util.WebRequestTools;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ImageGroupDisplayControllerTest {
    private static final String SCREEN_NAME = "tester";
    private static final String GROUP_NAME = "test-roll";
    private static final String IMAGE_GROUP_KEY = "imageGroup";
    private static final String FRAMES_KEY = "frames";
    private static final String IMAGE_GROUP_IS_PUBLIC_KEY = "imageGroupIsPublic";
    private AnnotationMethodHandlerAdapter handlerAdapter;
    private ImageGroupDisplayController controller;
    @Mock private ImageGroupRepository imageGroupRepository;
    @Mock private ImageSecurityService imageSecurityService;
    @Mock private UserRepository userRepository;
    @Mock private WebRequestTools webRequestTools;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        controller = new ImageGroupDisplayController(imageGroupRepository, imageSecurityService, userRepository, webRequestTools);
    }

    @Test
    public void testPlainGetDisplaysGroup() throws Exception {
        User tester = new User(SCREEN_NAME);
        ImageGroup imageGroup = new ImageGroup(GROUP_NAME, tester, ImageGroup.Type.ROLL);

        ModelAndView modelAndView = setupAndMockRequest(tester, imageGroup, new HashMap<String, String>());

        assertEquals("imageGroup", modelAndView.getViewName());
        
        Map<String,Object> model = modelAndView.getModel();

        ImageGroup resultGroup = (ImageGroup) model.get(IMAGE_GROUP_KEY);
        assertEquals(imageGroup, resultGroup);
        assertEquals(imageGroup.getFrames(), model.get(FRAMES_KEY));
        assertEquals(1, resultGroup.getFrames().size());
        assertEquals(1, ((Collection) model.get(FRAMES_KEY)).size());

        assertEquals(false, model.get(IMAGE_GROUP_IS_PUBLIC_KEY));
    }

    @Test
    public void testGetWithCreateRollParamDisplaysCreateRoll() throws Exception {
        User tester = new User(SCREEN_NAME);
        ImageGroup imageGroup = new ImageGroup(GROUP_NAME, tester, ImageGroup.Type.ROLL);

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("createRoll", "true");
        ModelAndView modelAndView = setupAndMockRequest(tester, imageGroup, paramMap);

        assertEquals("newGroup", modelAndView.getViewName());

        Map<String,Object> model = modelAndView.getModel();

        ImageGroup resultGroup = (ImageGroup) model.get(IMAGE_GROUP_KEY);
        assertEquals(imageGroup, resultGroup);
        assertEquals(imageGroup.getFrames(), model.get(FRAMES_KEY));
        assertEquals(1, resultGroup.getFrames().size());
        assertEquals(1, ((Collection) model.get(FRAMES_KEY)).size());

        assertEquals(false, model.get(IMAGE_GROUP_IS_PUBLIC_KEY));
    }

    private ModelAndView setupAndMockRequest(User tester, ImageGroup imageGroup, Map<String,String> paramMap) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rolls/tester/test-roll");
        for (String param : paramMap.keySet()) {
            request.addParameter(param, paramMap.get(param));
        }
        MockHttpServletResponse response = new MockHttpServletResponse();

        imageGroup.addFrame(new ImageFrame(new Photo()));

        when(webRequestTools.preHandlingFails(request, response, false)).thenReturn(false);
        when(userRepository.getByScreenName(SCREEN_NAME)).thenReturn(tester);
        when(imageGroupRepository.getByOwnerNameAndTypeWithFrames(tester, GROUP_NAME, ImageGroup.Type.ROLL)).thenReturn(imageGroup);

        return handlerAdapter.handle(request, response, controller);
    }
}
