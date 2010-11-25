package com.trailmagic.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.trailmagic.image.ImageGroup;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class WebRequestToolsTest {
    private WebRequestTools webRequestTools;
    
    @Before
    public void setUp() {
        webRequestTools = new WebRequestTools();
    }
    
    @Test(expected=MalformedUrlException.class)
    public void testGetImageRequestInfoNoPath() throws MalformedUrlException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/photo/");
        request.setContextPath("/photo");
        webRequestTools.getImageRequestInfo(request);
    }
    @Test(expected=MalformedUrlException.class)
    public void testGetImageRequestInfoBadGroupType() throws MalformedUrlException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/photo/pork/");
        request.setContextPath("/photo");
        webRequestTools.getImageRequestInfo(request);
    }
    
    @Test
    public void testGetImageRequestInfoRollsUserList() throws MalformedUrlException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/photo/rolls/");
        request.setContextPath("/photo");
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        assertEquals(ImageGroup.Type.ROLL, iri.getImageGroupType());
        assertNull(iri.getScreenName());
        assertNull(iri.getImageGroupName());
        assertNull(iri.getImageId());
    }

    @Test
    public void testGetImageRequestInfoAlbumsUserList() throws MalformedUrlException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/photo/albums/");
        request.setContextPath("/photo");
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        assertEquals(ImageGroup.Type.ALBUM, iri.getImageGroupType());
        assertNull(iri.getImageGroupName());
        assertNull(iri.getScreenName());
        assertNull(iri.getImageId());
    }
    @Test
    public void testGetImageRequestInfoGroupList() throws MalformedUrlException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/photo/rolls/oliver/");
        request.setContextPath("/photo");
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        assertEquals(ImageGroup.Type.ROLL, iri.getImageGroupType());
        assertEquals("oliver", iri.getScreenName());
        assertNull(iri.getImageGroupName());
        assertNull(iri.getImageId());
    }
    @Test
    public void testGetImageRequestInfoImageGroup() throws MalformedUrlException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/photo/rolls/oliver/monkeys/");
        request.setContextPath("/photo");
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        assertEquals(ImageGroup.Type.ROLL, iri.getImageGroupType());
        assertEquals("oliver", iri.getScreenName());
        assertEquals("monkeys", iri.getImageGroupName());
        assertNull(iri.getImageId());
    }
    @Test
    public void testGetImageRequestInfoImage() throws MalformedUrlException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/photo/rolls/oliver/monkeys/666");
        request.setContextPath("/photo");
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        assertEquals(ImageGroup.Type.ROLL, iri.getImageGroupType());
        assertEquals("oliver", iri.getScreenName());
        assertEquals("monkeys", iri.getImageGroupName());
        assertEquals(new Long(666L), iri.getImageId());
    }
    @Test
    public void testGetImageRequestInfoUserList() throws MalformedUrlException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/photo/rolls//");
        request.setContextPath("/photo");
        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(request);
        assertEquals(ImageGroup.Type.ROLL, iri.getImageGroupType());
        assertNull(iri.getImageGroupName());
        assertNull(iri.getScreenName());
        assertNull(iri.getImageId());
    }
}
