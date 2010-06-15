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
package com.trailmagic.image.impl;

import com.trailmagic.image.ImageGroup.Type;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.image.*;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.util.SecurityUtil;
import java.util.Collection;
import java.util.Date;
import java.io.InputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.Hibernate;

@Transactional
@Service("imageService")
public class ImageServiceImpl implements ImageService {
    private ImageGroupRepository imageGroupRepository;
    private ImageRepository imageRepository;
    private ImageSecurityService imageSecurityService;
    private ImageManifestationRepository imageManifestationRepository;
    private UserRepository userRepository;
    private SecurityUtil securityUtil;
    
    private static Logger log =
        LoggerFactory.getLogger(ImageServiceImpl.class);

    @SuppressWarnings({"SpringJavaAutowiringInspection"})
    @Autowired
    public ImageServiceImpl(ImageGroupRepository imageGroupRepository,
                            ImageRepository imageRepository,
                            ImageSecurityService imageSecurityService,
                            ImageManifestationRepository imageManifestationRepository,
                            UserRepository userRepository,
                            SecurityUtil securityUtil) {
        super();
        this.imageGroupRepository = imageGroupRepository;
        this.imageRepository = imageRepository;
        this.imageSecurityService = imageSecurityService;
        this.imageManifestationRepository = imageManifestationRepository;
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
    }

    public Photo createImage(ImageMetadata imageMetadata, InputStream inputStream, String contentType) throws IllegalStateException, IOException {
        Photo photo = createImage(imageMetadata);

        final HeavyImageManifestation manifestation = new HeavyImageManifestation();
        manifestation.setData(Hibernate.createBlob(inputStream));
        manifestation.setOriginal(true);
        manifestation.setFormat(contentType);
        photo.addManifestation(manifestation);

        saveNewImageManifestation(manifestation);
        imageRepository.save(photo);

        scheduleResize(photo);

        return photo;
    }

    private void scheduleResize(Photo photo) {
        log.info("Scheduling resize of image: " + photo.getDisplayName() + " (id=" + photo.getId() + ")");
        //TODO: implement
    }

    public Photo createImage(ImageMetadata imageData) throws IllegalStateException {
        Photo photo = new Photo();
        photo.setCaption(imageData.getCaption());
        photo.setName(imageData.getShortName());
        photo.setDisplayName(imageData.getDisplayName());
        photo.setCopyright(imageData.getCopyright());
        photo.setCreator(imageData.getCreator());
        final User currentUser = securityUtil.getCurrentUser();
        photo.setOwner(currentUser);
        if (imageData.getRollName() == null) {
            photo.setRoll(getDefaultRollForUser(currentUser));

        } else {
            photo.setRoll(imageGroupRepository.getRollByOwnerAndName(currentUser, imageData.getRollName()));
        }
        saveNewImage(photo);

        return photo;
    }

    private ImageGroup getDefaultRollForUser(User currentUser) {
        ImageGroup defaultRoll = imageGroupRepository.getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME);
        if (defaultRoll == null) {
            defaultRoll = new ImageGroup();
            defaultRoll.setType(ImageGroup.Type.ROLL);
            defaultRoll.setOwner(currentUser);
            defaultRoll.setSupergroup(null);
            defaultRoll.setUploadDate(new Date());
            defaultRoll.setName("uploads");
            defaultRoll.setDisplayName("Uploads");
            defaultRoll.setDescription("Uploaded images");
            imageGroupRepository.saveNewGroup(defaultRoll);
        }
        return defaultRoll;
    }

    public void saveNewImage(Image image) {
        log.info("Saving image: " + image);
        if (image.getOwner() == null) {
            if (securityUtil.getCurrentUser() != null) {
                image.setOwner(securityUtil.getCurrentUser());
            } else {
                throw new IllegalStateException("Can't save an image with no owner");
            }
        }

        imageRepository.saveNew(image);
        imageSecurityService.addOwnerAcl(image);
    }

    public void saveNewImageGroup(ImageGroup imageGroup) throws IllegalStateException {
        log.info("Saving image group: " + imageGroup);
        if (imageGroup.getOwner() == null) {
            if (securityUtil.getCurrentUser() != null) {
                imageGroup.setOwner(securityUtil.getCurrentUser());
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
        User owner = userRepository.getByScreenName(ownerName);
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