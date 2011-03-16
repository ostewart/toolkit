package com.trailmagic.image

import com.trailmagic.image.security.Identity
import java.sql.Blob
import reflect.BeanProperty

@Identity(classOf[ImageManifestation])
class HeavyImageManifestation() extends ImageManifestation {
  @BeanProperty var data: Blob = null;

  def this(data: Blob) {
    this()
    this.data = data
  }
}
