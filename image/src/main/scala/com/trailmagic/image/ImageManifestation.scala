package com.trailmagic.image

import com.trailmagic.user.Owned
import com.trailmagic.image.security.IdentityProxy
import reflect.BeanProperty

/**
 * This class maps the metadata of the manifestation, while its subclass,
 * the <code>HeavyImageManifestation</code> also maps the data.
 */
class ImageManifestation extends Comparable[ImageManifestation] with Owned {
  @BeanProperty var id: Long = 0L
  @IdentityProxy
  @BeanProperty var image: Image = null
  @BeanProperty var height: Int = 0
  @BeanProperty var width: Int = 0
  @BeanProperty var format: String = null
  @BeanProperty var original: Boolean = false
  @BeanProperty var name: String = null

  def getOwner = image.owner

  def area = height * width
  def getArea = area

  def isOriginal = original

  def compareTo(other: ImageManifestation): Int = {
    this.area - other.area match {
      case 0 => (this.getId - other.getId).intValue
      case difference => difference
    }
  }

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
    return getClass + "(id=" + id + "; name=" + name + "; height=" + height + "; width=" + width + "; format=" + format + "; original=" + original
  }
}