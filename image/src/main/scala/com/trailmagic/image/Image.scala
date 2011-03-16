/*
 * Copyright (c) 2006 Oliver Stewart.  All Rights Reserved.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.trailmagic.image

import com.trailmagic.image.security.AccessControlled
import com.trailmagic.user.Owned
import com.trailmagic.user.User
import java.util.SortedSet
import java.util.TreeSet
import reflect.BeanProperty
import java.lang.Integer

class Image extends Owned with AccessControlled {
  @BeanProperty
  var id:  Long = 0L
  @BeanProperty
  var name: String = null
  @BeanProperty
  var displayName: String = null
  @BeanProperty
  var caption: String = null
  @BeanProperty
  var copyright: String = null
  @BeanProperty
  var creator: String = null
  @BeanProperty
  var manifestations: SortedSet[ImageManifestation] = new TreeSet[ImageManifestation]
  @BeanProperty
  var owner: User = null
  @BeanProperty
  var imageCD: ImageCD = null
  @BeanProperty
  var number: Integer = null

  def this(owner: User) {
    this ()
    this.owner = owner
  }

  def this(name: String, owner: User) {
    this ()
    this.name = name
    this.owner = owner
  }

  def this(image: Image) {
    this ()
    setName(image.getName)
    setDisplayName(image.getDisplayName)
    setCaption(image.getCaption)
    setCopyright(image.getCopyright)
    setCreator(image.getCreator)
    setOwner(image.getOwner)
    setImageCD(image.getImageCD)
    setManifestations(image.getManifestations)
    setNumber(image.getNumber)
  }

  def copyFrom(that: Image) {
    this.id = that.id
    this.name = that.name
    this.displayName = that.displayName
    this.caption = that.caption
    this.copyright = that.copyright
    this.creator = that.creator
    this.manifestations = that.manifestations
    this.owner = that.owner
    this.imageCD = that.imageCD
    this.number = that.number
  }

  def addManifestation(im: ImageManifestation): Unit = {
    manifestations.add(im)
    im.setImage(this)
  }


  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[Image])) {
      return false
    }
    var that: Image = obj.asInstanceOf[Image]
    return this.getOwner.equals((obj.asInstanceOf[Image]).getOwner) && (namesAreNullAndIdIsEqual(that) || this.getName.equals((obj.asInstanceOf[Image]).getName))
  }

  private def namesAreNullAndIdIsEqual(that: Image): Boolean = {
    return this.getName == null && that.getName == null && this.getId == that.getId
  }

  override def hashCode: Int = {
    var result: Int = if (name != null) name.hashCode else 0
    result = 31 * result + (if (owner != null) owner.hashCode else 0)
    return result
  }

  override def toString: String = {
    return getClass.getName + "{id=" + id + ", name=" + getName + "}"
  }
}