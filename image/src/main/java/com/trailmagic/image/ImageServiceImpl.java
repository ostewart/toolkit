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
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import com.trailmagic.util.SecurityUtil;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ImageServiceImpl implements ImageService {
    private ImageGroupRepository imageGroupRepository;
    private ImageRepository imageRepository;
    private ImageSecurityService imageSecurityService;
    private ImageManifestationRepository imageManifestationRepository;
    private UserFactory userFactory;
    
    private static Logger log =
        Logger.getLogger(ImageServiceImpl.class);

    public ImageServiceImpl(ImageGroupRepository imageGroupRepository,
            ImageRepository imageRepository,
            ImageSecurityService imageSecurityService,
            ImageManifestationRepository imageManifestationRepository,
            UserFactory userFactory) {
        super();
        this.imageGroupRepository = imageGroupRepository;
        this.imageRepository = imageRepository;
        this.imageSecurityService = imageSecurityService;
        this.imageManifestationRepository = imageManifestationRepository;
        this.userFactory = userFactory;
    }
    
    public void saveNewImage(Image image) {
        log.info("Saving image: " + image);
        if (image.getOwner() == null) {
            if (SecurityUtil.getCurrentUser() != null) {
                image.setOwner(SecurityUtil.getCurrentUser());
            } else {
                throw new IllegalStateException("Can't save an image with no owner");
            }
        }

        imageRepository.saveNew(image);
        imageSecurityService.addOwnerAcl(image);
    }

    public void saveNewImageGroup(ImageGroup imageGroup) {
        log.info("Saving image group: " + imageGroup);
        if (imageGroup.getOwner() == null) {
            if (SecurityUtil.getCurrentUser() != null) {
                imageGroup.setOwner(SecurityUtil.getCurrentUser());
            } else {
                throw new IllegalStateException("Can't save a roll with no owner");
            }
        }
        
        if (imageGroup.getPreviewImage() == null
                && imageGroup.getFrames() != null
                && imageGroup.getFrames().size() > 0) {
            imageGroup.setPreviewImage(imageGroup.getFrames().first().getImage());
            if (log.isDebugEnabled()) {
                log.debug("Set missing preview image to first image on group: "
                          + imageGroup.getName());
            }
        }

        imageGroupRepository.saveNewGroup(imageGroup);
        imageSecurityService.addOwnerAcl(imageGroup);
    }
    
    public void saveNewImageFrame(ImageFrame imageFrame) {
        imageGroupRepository.saveFrame(imageFrame);
        imageSecurityService.addOwnerAcl(imageFrame);
    }
    
    public void saveNewImageManifestation(HeavyImageManifestation imageManifestation) {
        imageManifestationRepository.saveNewImageManifestation(imageManifestation);
        imageSecurityService.addOwnerAcl(imageManifestation);
        
        log.info("Saved image manifestation (before flush/evict): "
                 + imageManifestation);
        // for now we'll clear/evict here since the normal case is probably
        // that we want this out of memory quickly
        imageManifestationRepository.cleanFromSession(imageManifestation);
    }

    public ImageFrame addImageToGroup(Image image, ImageGroup group,
                                      int position) {
        ImageFrame frame = new ImageFrame();
        frame.setImageGroup(group);
        frame.setImage(image);
        frame.setPosition(position);

        imageGroupRepository.saveFrame(frame);
        imageSecurityService.addOwnerAcl(frame);

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
        imageSecurityService.makePublic(group);
        log.info("Added public permission for group: "
                   + group.getName());

        Collection<ImageFrame> frames = group.getFrames();

        for (ImageFrame frame : frames) {
            imageSecurityService.makePublic(frame);
            log.info("Added public permission for frame: "
                       + frame.getPosition() + " of group "
                       + group.getName());

            Image image = frame.getImage();
            imageSecurityService.makePublic(image);
            log.info("Added public permission for image: "
                       + image.getDisplayName());

            for (ImageManifestation mf : image.getManifestations()) {
                imageSecurityService.makePublic(mf);
                log.info("Added public permission for "
                           + "manifestation: "
                           + mf.getHeight() + "x" + mf.getWidth());
            }
        }
    }

    public void makeImageGroupPublic(String ownerName, Type type, String imageGroupName)
            throws NoSuchImageGroupException {
        User owner = userFactory.getByScreenName(ownerName);
        ImageGroup group =
            imageGroupRepository.getByOwnerNameAndTypeWithFrames(owner,
                                                      imageGroupName,
                                                      type);
        if (group == null) {
            log.error("No " + type + " found with name " + imageGroupName
                        + " owned by " + owner);
        }
        makeImageGroupPublic(group);
    }
    
    public void setImageGroupPreview(long imageGroupId, long imageId)
            throws NoSuchImageGroupException, NoSuchImageException {
        ImageGroup imageGroup = imageGroupRepository.loadById(imageGroupId);
        Image image = imageRepository.loadById(imageId); 
        imageGroup.setPreviewImage(image);
        imageGroupRepository.saveGroup(imageGroup);
    }
}