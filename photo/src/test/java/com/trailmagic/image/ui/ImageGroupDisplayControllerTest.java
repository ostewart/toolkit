package com.trailmagic.image.ui;

import com.trailmagic.image.*;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.web.util.WebRequestTools;
import org.aspectj.lang.Aspects;
import org.aspectj.lang.NoAspectBoundException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.intercept.aspectj.AspectJMethodSecurityInterceptor;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
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
    @Mock private ImageService imageService;

    @Before
    public void setUp() throws Exception {
        disableSecurityInterceptor();
        MockitoAnnotations.initMocks(this);
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        controller = new ImageGroupDisplayController(imageGroupRepository, imageSecurityService, userRepository, webRequestTools, imageService);
    }

    private void disableSecurityInterceptor() {
        try {
            final AspectJMethodSecurityInterceptor interceptor = new AspectJMethodSecurityInterceptor();
            interceptor.setSecurityMetadataSource(new MapBasedMethodSecurityMetadataSource());
            Aspects.aspectOf(ImageSecurityAspect.class).setSecurityInterceptor(interceptor);
        } catch (NoAspectBoundException e) {
            // AspectJ isn't on
        }
    }

    @Test
    public void testPlainGetDisplaysGroup() throws Exception {
        User tester = testUser();
        ImageGroup imageGroup = testImageGroup(tester);

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

    private User testUser() {
        return new User(SCREEN_NAME);
    }

    @Test
    public void testGetWithCreateRollParamDisplaysCreateRoll() throws Exception {
        User tester = testUser();
        ImageGroup imageGroup = testImageGroup(tester);

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

    private ImageGroup testImageGroup(User tester) {
        return new ImageGroup(GROUP_NAME, tester, ImageGroup.Type.ROLL);
    }

    private ModelAndView setupAndMockRequest(User tester, ImageGroup imageGroup, Map<String,String> paramMap) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rolls/tester/" + GROUP_NAME);
        for (String param : paramMap.keySet()) {
            request.addParameter(param, paramMap.get(param));
        }
        MockHttpServletResponse response = new MockHttpServletResponse();

        setupUserAndGroupMocks(tester, imageGroup);

        when(webRequestTools.preHandlingFails(request, response, false)).thenReturn(false);

        return handlerAdapter.handle(request, response, controller);
    }

    private void setupUserAndGroupMocks(User tester, ImageGroup imageGroup) {
        imageGroup.addFrame(new ImageFrame(new Photo()));
        when(userRepository.getByScreenName(SCREEN_NAME)).thenReturn(tester);
        when(imageGroupRepository.getByOwnerNameAndTypeWithFrames(tester, GROUP_NAME, ImageGroup.Type.ROLL)).thenReturn(imageGroup);
    }

    @Test
    @Ignore
    public void testPostWithNewRollCreatesRollAndRedirectsToIt() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rolls/tester/" + GROUP_NAME);

        request.addParameter("rollName", "new roll");
        request.addParameter("selectedFrames", "1234");
        request.addParameter("selectedFrames", "4567");
        request.setContent("rollName=new roll&selectedFrames=1234&selectedFrames=4567".getBytes());
        request.setContentType("application/x-www-form-urlencoded");
        MockHttpServletResponse response = new MockHttpServletResponse();

        User tester = testUser();
        setupUserAndGroupMocks(tester, testImageGroup(tester));

        ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals("redirect:/rolls/tester/new-roll", modelAndView.getViewName());
    }
}
