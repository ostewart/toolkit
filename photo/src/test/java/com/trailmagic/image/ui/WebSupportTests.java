package com.trailmagic.image.ui;

import javax.servlet.http.HttpServletRequest;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class WebSupportTests extends TestCase {
    public void testExtractImageIdFromRequest() {
        HttpServletRequest req =
            new MockHttpServletRequest("GET",
                                       "/albums/oliver/costa-rica-2006/1127");
        Long id = WebSupport.extractImageIdFromRequest(req);
        assertEquals(new Long(1127), id);
        
        try {
            req = new MockHttpServletRequest("GET",
                                             "/albums/oliver/costa-rica-2006/");
            id = WebSupport.extractImageIdFromRequest(req);
            fail("Expected invalid request exception");
        } catch (IllegalArgumentException e) {
            // you win
        }

    }
}
