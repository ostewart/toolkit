package com.trailmagic.image

import com.trailmagic.user.Owned
import com.trailmagic.user.User
import com.trailmagic.image.security.IdentityProxy
import java.util.Iterator
import java.util.SortedSet
import reflect.BeanProperty

class ImageFrame() extends Owned with Comparable[ImageFrame] {
  @BeanProperty var id: Long = 0L
  @BeanProperty var imageGroup: ImageGroup = null
  @BeanProperty var position: Int = 0
  @BeanProperty @IdentityProxy var image: Image = null
  @BeanProperty var caption: String = null

  def this(image: Image) {
    this ()
    this.image = image
  }

  @SuppressWarnings(Array("RedundantIfStatement"))
  override def equals(o: Any): Boolean = {
    if (this == o) return true
    if (!(o.isInstanceOf[ImageFrame])) return false
    var frame: ImageFrame = o.asInstanceOf[ImageFrame]
    if (position != frame.position) return false
    if (if (image != null) !image.equals(frame.image) else frame.image != null) return false
    if (if (imageGroup != null) !imageGroup.equals(frame.imageGroup) else frame.imageGroup != null) return false
    return true
  }

  override def hashCode: Int = {
    var result: Int = if (imageGroup != null) imageGroup.hashCode else 0
    result = 31 * result + position
    result = 31 * result + (if (image != null) image.hashCode else 0)
    return result
  }

  def compareTo(other: ImageFrame): Int = {
    val positionDifference: Int = Integer.valueOf(position).compareTo(other.position)
    if (imageGroup == null || imageGroup == other.imageGroup || imageGroup.equals(other.imageGroup)) {
      return positionDifference
    }
    else {
      return imageGroup.getName.compareTo(other.getImageGroup.getName) + positionDifference
    }
  }

  /**
   * The frame is always owned by the owner of the image group,
   * since it's really only a link class.  This could cause mayhem if
   * the permissions on an image change to disallow access to the group
   * owner, but it's a tricky situation anyway, and the frame really
   * properly belongs to the group.
   **/
  @SuppressWarnings(Array("JpaAttributeMemberSignatureInspection", "JpaAttributeTypeInspection"))
  def getOwner: User = {
    return imageGroup.getOwner
  }

  override def toString: String = {
    return getClass + "(id=" + id + "; position=" + position + "; image=" + image + "; imageGroup=" + imageGroup + ")"
  }

  def previous: ImageFrame = {
    val headSet: SortedSet[ImageFrame] = imageGroup.getFrames.headSet(this)
    if (!headSet.isEmpty) {
      return headSet.last
    }
    return null
  }

  def next: ImageFrame = {
    val framesIter: Iterator[ImageFrame] = imageGroup.getFrames.tailSet(this).iterator
    framesIter.next
    if (framesIter.hasNext) {
      return framesIter.next
    }
    return null
  }

}