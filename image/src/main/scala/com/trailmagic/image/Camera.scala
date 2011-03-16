package com.trailmagic.image

import reflect.BeanProperty

class Camera() {
  @BeanProperty var id: Long = 0L
  @BeanProperty var name: String = null
  @BeanProperty var manufacturer: String = null
  @BeanProperty var format: String = null

  def this(id: Long) {
    this ()
    this.id = id
  }
}