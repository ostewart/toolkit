package com.trailmagic.image.ui.upload

import org.junit.{Before, Test}
import com.trailmagic.util.SecurityUtil
import org.mockito.{MockitoAnnotations, Mock}
import org.mockito.Mockito.{times, verify, when}
import org.springframework.mock.web.{MockHttpServletResponse, MockHttpServletRequest}
import com.trailmagic.image.{Photo, ImageService}
import com.trailmagic.user.User

class SimpleImageUploadControllerTest {
  var controller: SimpleImageUploadController = _
  @Mock var imageService: ImageService = _
  @Mock var securityUtil: SecurityUtil = _

  @Before
  def setUp() {
    MockitoAnnotations.initMocks(this)
    controller = new SimpleImageUploadController(imageService, securityUtil)
  }

  @Test
  def testCreatesDefaultImageWhenNoPositionParameterIsGiven {
    val fileName = "niceImage.jpg"
    when(imageService.createDefaultImage(fileName)).thenReturn(new Photo())
    when(securityUtil.getCurrentUser).thenReturn(new User("tester"))

    controller.handleUpload(null, fileName, new MockHttpServletRequest, new MockHttpServletResponse)

    verify(imageService, times(1)).createDefaultImage(fileName)
  }

  @Test
  def testCreatesImageAtPositionWhenPositionParameterSupplied {
    val fileName = "niceImage.jpg"
    val pos = 6
    val req = new MockHttpServletRequest
    when(imageService.createImageAtPosition(fileName, req.getInputStream, pos)).thenReturn(new Photo())
    when(securityUtil.getCurrentUser).thenReturn(new User("tester"))

    controller.handleUpload(pos, fileName, req, new MockHttpServletResponse)

    verify(imageService, times(1)).createImageAtPosition(fileName, req.getInputStream, pos)
  }
}