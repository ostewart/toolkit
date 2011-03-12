package com.trailmagic.resizer

import org.junit.Assert._
import org.mockito.Mockito.{mock, times, verify, when}
import org.junit.{Test, Before}
import scalaj.collection.Imports._
import java.io.{InputStream, ByteArrayInputStream, File}
import org.mockito.Matchers.{isA, anyInt}
import org.mockito.{Matchers, Mockito, MockitoAnnotations, Mock}

class ImageResizeServiceImplTest {
  @Mock
  private var imageResizer: ImageResizer = _
  private var imageResizeService: ImageResizeService = _

  @Before
  def setUp() {
    MockitoAnnotations.initMocks(this)
    imageResizeService = new ImageResizeServiceImpl(imageResizer)
  }

  @Test
  def testScheduleResize {
    val srcFileInfo = new ImageFileInfo(2050, 3066, "image/jpeg")
    mockIdentifyToReturnSrcFileInfoAndSizes(srcFileInfo, 128, 256, 512, 1024, 2048)
    val srcFile = mockResizerToWriteAndResize


    val files = imageResizeService.scheduleResize(new ByteArrayInputStream(Array[Byte]()))

    assertResizeCalledWithSizes(srcFileInfo, 128, 256, 512, 1024, 2048)

    assertEquals(5, files.size)
    files.foreach((file => assertNotNull(file.getFile())))
    verify(srcFile).delete()
  }

  private def mockResizerToWriteAndResize: File = {
    val srcFile = mock(classOf[File])
    val resultFile = mock(classOf[File])
    when(imageResizer.writeToTempFile(isA(classOf[InputStream]))).thenReturn(srcFile)
    when(imageResizer.resizeImage(isA(classOf[File]), isA(classOf[ImageFileInfo]), anyInt())).thenReturn(resultFile)
    return srcFile
  }

  private def mockIdentifyToReturnSrcFileInfoAndSizes(srcFileInfo: ImageFileInfo, sizes: Int*) {
    (List[ImageFileInfo]() /: sizes) {
      (result, size) =>
        new ImageFileInfo(size, size * 2, "image/jpeg") :: result
    }
    when(imageResizer.identify(isA(classOf[File]))).thenReturn(srcFileInfo)
  }


  @Test
  def testOnlyCreatesSmallerSizeImages {
    val srcFileInfo = new ImageFileInfo(200, 400, "image/jpeg")
    mockIdentifyToReturnSrcFileInfoAndSizes(srcFileInfo, 128)
    val srcFile = mockResizerToWriteAndResize

    val files = imageResizeService.scheduleResize(new ByteArrayInputStream(Array[Byte]()))

    assertResizeCalledWithSizes(srcFileInfo, 128)

    assertEquals(1, files.size())
    files.foreach((file) => assertNotNull(file.getFile()))
    verify(srcFile).delete()
  }

  @Test
  def testOnlyCreatesEqualAndSmallerSizeImages {
    val srcFileInfo = new ImageFileInfo(256, 400, "image/jpeg")
    mockIdentifyToReturnSrcFileInfoAndSizes(srcFileInfo, 128, 256)
    val srcFile = mockResizerToWriteAndResize

    val files = imageResizeService.scheduleResize(new ByteArrayInputStream(Array[Byte]()))

    assertResizeCalledWithSizes(srcFileInfo, 128, 256)

    assertEquals(2, files.size())
    files.foreach((file) => assertNotNull(file.getFile()))
    verify(srcFile).delete()
  }

  private def assertResizeCalledWithSizes(srcFileInfo: ImageFileInfo, sizes: Int*) {
    sizes.foreach((size) => verify(imageResizer, times(1)).resizeImage(isA(classOf[File]), Matchers.eq(srcFileInfo), Matchers.eq(size)))
    verify(imageResizer, Mockito.times(sizes.length)).resizeImage(isA(classOf[File]), Matchers.eq(srcFileInfo), anyInt())
  }
}
