package com.trailmagic.image

import com.trailmagic.image.security.AccessControlled
import com.trailmagic.user.Owned
import com.trailmagic.user.User
import java.util.Collection
import java.util.Date
import java.util.SortedSet
import java.util.TreeSet
import reflect.BeanProperty

object ImageGroup {
  val DEFAULT_ROLL_NAME: String = "uploads"
}

class ImageGroup(@BeanProperty var name: String,
                 @BeanProperty var owner: User,
                 @BeanProperty var `type` : ImageGroupType) extends Owned with AccessControlled {
  @BeanProperty var id: java.lang.Long = 0L
  @BeanProperty var displayName: String = null
  @BeanProperty var description: String = null
  @BeanProperty var uploadDate: Date = null
  @BeanProperty var frames: SortedSet[ImageFrame] = new TreeSet[ImageFrame]
  @BeanProperty var subgroups: Collection[ImageGroup] = null
  @BeanProperty var supergroup: ImageGroup = null
  @BeanProperty var previewImage: Image = null

  def this() {
    this (null, null, null)
  }

  def nextFramePosition: Int = {
    if (frames.isEmpty) {
      return 1
    }
    else {
      return frames.last.getPosition + 1
    }
  }

  @SuppressWarnings(Array("JpaAttributeMemberSignatureInspection"))
  def getTypeDisplay: String = {
    return `type`.getDisplayString
  }

  def addFrame(frame: ImageFrame): Unit = {
    frames.add(frame)
    frame.setImageGroup(this)
  }

  @SuppressWarnings(Array("JpaAttributeMemberSignatureInspection"))
  def getNextFrameNumber: Int = {
    var lastFrame: ImageFrame = frames.last
    return lastFrame.getPosition + 1
  }

  def addSubgroup(group: ImageGroup): Unit = {
    group.setSupergroup(this)
    subgroups.add(group)
  }


  override def equals(obj: Any): Boolean = {
    return (obj.isInstanceOf[ImageGroup]) && (this.getName.equals((obj.asInstanceOf[ImageGroup]).getName)) && (this.getOwner.equals((obj.asInstanceOf[ImageGroup]).getOwner)) && (this.getType.equals((obj.asInstanceOf[ImageGroup]).getType))
  }

  override def hashCode: Int = {
    return getName.hashCode + getOwner.hashCode + getType.hashCode
  }

  override def toString: String = {
    return "ImageGroup(id=" + id + "; type=" + `type` + "; name=" + name + "; owner=" + owner + ")"
  }

}