package com.trailmagic.image

import com.trailmagic.image.security.Identity
import com.trailmagic.user.User
import java.util.Date
import reflect.BeanProperty

@Identity(classOf[Image])
class Photo extends Image {
  @BeanProperty var lens: Lens = null
  @BeanProperty var camera: Camera = null
  @BeanProperty var notes: String = null
  @BeanProperty var captureDate: Date = null
  @BeanProperty var roll: ImageGroup = null

  def this(name: String, owner: User) = {
    this()
    this.name = name
    this.owner = owner
  }

  def this(image: Image) {
    this()
    copyFrom(image)
  }

  def copyFrom(that: Photo) {
    super.copyFrom(that)
    this.lens = that.lens
    this.camera = that.camera
    this.notes = that.notes
    this.captureDate = that.captureDate
    this.roll = that.roll
  }

  def this(photo: Photo) {
    this()
    copyFrom(photo)
  }
}