package com.trailmagic.image

import com.trailmagic.user.Owned
import com.trailmagic.image.security.IdentityProxy
import reflect.BeanProperty

/**
 * This class maps the metadata of the manifestation, while its subclass,
 * the <code>HeavyImageManifestation</code> also maps the data.
 */
class ImageManifestation(img: Image = null,
                          @BeanProperty var width: Int = 0,
                          @BeanProperty var height: Int = 0,
                          @BeanProperty var original: Boolean = false) extends Owned with Ordered[ImageManifestation] {
  @BeanProperty var id: Long = 0L
  @IdentityProxy @BeanProperty var image = img
  @BeanProperty var format: String = null
  @BeanProperty var name: String = null

  def this() = {
    this(null, 0, 0, false)
  }

  def getOwner = image.owner

  def area = height * width
  def getArea = area

  def isOriginal = original

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[ImageManifestation])) {
      return false
    }
    val that: ImageManifestation = obj.asInstanceOf[ImageManifestation]
    return compareTo(that) == 0
  }

  override def hashCode: Int = {
    return area
  }

  override def toString: String = {
    return getClass + "(id=" + id + "; name=" + name + "; width=" + width + "; height=" + height + "; format=" + format + "; original=" + original
  }

  def compare(that: ImageManifestation) = {
    this.area - that.area match {
      case 0 => (this.getId - that.getId).intValue
      case difference => difference
    }
  }
}