package com.trailmagic.image

import java.util.Collection
import reflect.BeanProperty

class ImageCD() {
  @BeanProperty var id: Long = 0L
  @BeanProperty var number: Int = 0
  @BeanProperty var description: String = null
  @BeanProperty var images: Collection[_] = null
}