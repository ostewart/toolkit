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
package com.trailmagic.image;

import com.trailmagic.image.ImageGroup.Type;
import com.trailmagic.image.security.ImageSecurityFactory;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ImageManagerBackend implements ImageManager {
    private ImageGroupFactory m_imageGroupFactory;
    private ImageFactory m_imageFactory;
    private ImageSecurityFactory m_imageSecurityFactory;
    private UserFactory m_userFactory;
    
    private static Logger s_log =
        Logger.getLogger(ImageManagerBackend.class);

    public void setImageGroupFactory(ImageGroupFactory factory) {
        m_imageGroupFactory = factory;
    }

    public void setImageFactory(ImageFactory factory) {
        m_imageFactory = factory;
    }

    public void setImageSecurityFactory(ImageSecurityFactory factory) {
        m_imageSecurityFactory = factory;
    }

    public void setUserFactory(UserFactory userFactory) {
        m_userFactory = userFactory;
    }

    public ImageGroup createImageGroup(ImageGroup.Type type,
                                       User owner, String name) {
        ImageGroup newGroup = m_imageGroupFactory.createImageGroup(type);
        newGroup.setOwner(owner);
        m_imageSecurityFactory.addOwnerAcl(newGroup);
        newGroup.setName(name);

        m_imageGroupFactory.saveGroup(newGroup);
        return newGroup;
    }

    public Photo addPhoto(User owner, ImageGroup roll, String name) {
        Photo newPhoto = m_imageFactory.createPhoto();
        newPhoto.setOwner(owner);
        m_imageSecurityFactory.addOwnerAcl(newPhoto);
        newPhoto.setName(name);
        m_imageFactory.save(newPhoto);
        return newPhoto;
    }
    
    public ImageGroup addImageGroup(User owner, ImageGroup.Type type,
                                    String name) {
        ImageGroup group = m_imageGroupFactory.createImageGroup(type);
        group.setOwner(owner);
        m_imageSecurityFactory.addOwnerAcl(group);
        group.setName(name);
        m_imageGroupFactory.saveGroup(group);
        return group;
    }

    public ImageFrame addImageToGroup(Image image, ImageGroup group,
                                      int position) {
        ImageFrame frame = new ImageFrame();
        frame.setImageGroup(group);
        frame.setImage(image);
        frame.setPosition(position);


        // XXX: have to save the image first
        //do we?        m_session.saveOrUpdate(m_image);

            // this might happen twice, but i think that's okay
        m_imageSecurityFactory.addOwnerAcl(image);
        m_imageGroupFactory.saveFrame(frame);
        m_imageSecurityFactory.addOwnerAcl(frame);

        return frame;
    }

        // get DAO
        // create group
        // set properties
        // assign owner
        // assign perms
        // save object
        // return

    // for add to group, need accurate position count
    // (i.e. without security filtering)
    
    
    public void makeImageGroupPublic(ImageGroup group) {
        m_imageSecurityFactory.makePublic(group);
        s_log.info("Added public permission for group: "
                   + group.getName());

        Collection<ImageFrame> frames = group.getFrames();

        for (ImageFrame frame : frames) {
            m_imageSecurityFactory.makePublic(frame);
            s_log.info("Added public permission for frame: "
                       + frame.getPosition() + " of group "
                       + group.getName());

            Image image = frame.getImage();
            m_imageSecurityFactory.makePublic(image);
            s_log.info("Added public permission for image: "
                       + image.getDisplayName());

            for (ImageManifestation mf : image.getManifestations()) {
                m_imageSecurityFactory.makePublic(mf);
                s_log.info("Added public permission for "
                           + "manifestation: "
                           + mf.getHeight() + "x" + mf.getWidth());
            }
        }
    }

    public void makeImageGroupPublic(String ownerName, Type type, String imageGroupName) {
        User owner = m_userFactory.getByScreenName(ownerName);
        ImageGroup group =
            m_imageGroupFactory.getByOwnerNameAndType(owner,
                                                      imageGroupName,
                                                      type);
        if (group == null) {
            s_log.error("No " + type + " found with name " + imageGroupName
                        + " owned by " + owner);
        }
        makeImageGroupPublic(group);
    }
}