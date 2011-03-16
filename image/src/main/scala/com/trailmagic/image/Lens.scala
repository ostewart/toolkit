package com.trailmagic.image

import reflect.BeanProperty

class Lens() {
  @BeanProperty var id: Long = 0L
  @BeanProperty var name: String = null
  @BeanProperty var manufacturer: String = null
  @BeanProperty var focalLength: Int = 0
  @BeanProperty var minAperature: Int = 0
  @BeanProperty var maxAperature: Int = 0

  def this(id: Long) {
    this ()
    this.id = id
  }
}