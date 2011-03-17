package com.trailmagic.image

import java.io.{InputStream, IOException}
import com.trailmagic.user.User
import scala.collection.mutable.Set

trait ImageService {
  @throws(classOf[IllegalStateException])
  @throws(classOf[IOException])
  def createDefaultImage(fileName: String): Photo

  @throws(classOf[IOException])
  def createImageAtPosition(fileName: String, inputStream: InputStream, position: Int): Photo

  @throws(classOf[IllegalStateException])
  def createImage(imageData: ImageMetadata): Photo

  def addImageToGroup(image: Image, group: ImageGroup, position: Int): ImageFrame

  def addImageToGroup(image: Image, group: ImageGroup): ImageFrame

  def makeImageGroupAndImagesPublic(group: ImageGroup)

  @throws(classOf[NoSuchImageException])
  def makeImageGroupAndImagesPublic(ownerName: String, groupType: ImageGroupType, imageGroupName: String)

  @throws(classOf[NoSuchImageGroupException])
  def setImageGroupPreview(imageGroupId: Long, imageId: Long)

  def findNamedOrDefaultRoll(rollName: String, owner: User): ImageGroup

  def findOrCreateDefaultRollForUser(currentUser: User): ImageGroup

  @throws(classOf[IOException])
  def createManifestations(photo: Photo, imageDataInputStream: InputStream)

  def createRollWithFrames(rollName: String, selectedFrameIds: Set[Long])
}
