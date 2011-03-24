package com.trailmagic.image

import org.junit.Assert.{assertEquals, assertNull}
import org.junit.Test

class ImageTest {
  val image = new Image
  val manifestations = List(
    new ImageManifestation(image, 192, 128),
    new ImageManifestation(image, 384, 256),
    new ImageManifestation(image, 768, 512),
    new ImageManifestation(image, 1536, 1024),
    new ImageManifestation(image, 3072, 2048),
    new ImageManifestation(image, 3400, 2400, true)
  )
  manifestations.foreach {
    image.addManifestation(_)
  }

  @Test
  def testReturnsLabeledManifestation {
    assertEquals(manifestations.head, image.manifestationByLabel("thumbnail"))
    assertEquals(manifestations.drop(1).head, image.manifestationByLabel("small"))
    assertEquals(manifestations.drop(2).head, image.manifestationByLabel("medium"))
    assertEquals(manifestations.drop(3).head, image.manifestationByLabel("large"))
    assertEquals(manifestations.drop(4).head, image.manifestationByLabel("huge"))
  }

  @Test
  def testReturnsManifestationBySize {
    assertEquals(6, image.manifestations.size)

    assertEquals(manifestations(0), image.manifestationClosestTo(190, 100))
    assertEquals(manifestations(1), image.manifestationClosestTo(384, 256))
    assertEquals(manifestations(2), image.manifestationClosestTo(800, 500))
    assertEquals(manifestations(3), image.manifestationClosestTo(1500, 1000))
    assertEquals(manifestations(4), image.manifestationClosestTo(3000, 2048))
  }

  @Test
  def testDoesNotReturnOriginalManifestation {
    assertEquals(manifestations(4), image.manifestationClosestTo(3400, 2400))
  }
  
  @Test
  def testManifestationAccessorsYieldNullWhenNoManifestations {
    val image = new Image
    assertNull(image.manifestationClosestTo(192*128))
    assertNull(image.manifestationClosestTo(192, 128))
    assertNull(image.manifestationByLabel("small"))
  }

  @Test
  def testTranslatesLabelToArea {
    assertEquals(192 * 128, image.labelToArea("thumbnail"))
    assertEquals(384 * 256, image.labelToArea("small"))
    assertEquals(768 * 512, image.labelToArea("medium"))
    assertEquals(1536 * 1024, image.labelToArea("large"))
    assertEquals(3072 * 2048, image.labelToArea("huge"))
  }

  @Test
  def testFindsOriginalManifestation {
    assertEquals(manifestations(5), image.originalManifestation)
  }
}