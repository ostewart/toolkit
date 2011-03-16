package com.trailmagic.image

import reflect.BeanProperty

class ImageMetadata {
  @BeanProperty var shortName: String = null
  @BeanProperty var displayName: String = null
  @BeanProperty var caption: String = null
  @BeanProperty var copyright: String = null
  @BeanProperty var creator: String = null
  @BeanProperty var rollName: String = null
  @BeanProperty var position: java.lang.Integer = null
}