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

import com.trailmagic.image.*;
import com.trailmagic.image.ImageGroup.Type;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.util.SecurityUtil;
import com.trailmagic.util.TimeSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;

@Transactional
@Service("imageService")
public class ImageServiceImpl implements ImageService {
    private SecurityUtil securityUtil;

    private static Logger log =
            LoggerFactory.getLogger(ImageServiceImpl.class);
    private ImageInitializer imageInitializer;
    private TimeSource timeSource;
    private ImageRepository imageRepository;
    private ImageGroupRepository imageGroupRepository;
    private UserRepository userRepository;
    private ImageSecurityService imageSecurityService;
    private ImageManifestationService imageManifestationService;

    @SuppressWarnings({"SpringJavaAutowiringInspection"})
    @Autowired
    public ImageServiceImpl(ImageGroupRepository imageGroupRepository,
                            ImageRepository imageRepository,
                            ImageSecurityService imageSecurityService,
                            UserRepository userRepository,
                            SecurityUtil securityUtil,
                            ImageInitializer imageInitializer,
                            TimeSource timeSource,
                            ImageManifestationService imageManifestationService) {
        super();
        this.imageGroupRepository = imageGroupRepository;
        this.imageRepository = imageRepository;
        this.imageSecurityService = imageSecurityService;
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.imageInitializer = imageInitializer;
        this.timeSource = timeSource;
        this.imageManifestationService = imageManifestationService;
    }

    @Override
    public Photo createImage(InputStream inputStream) throws IllegalStateException, IOException {
        ImageMetadata imageMetadata = new ImageMetadata();

        imageMetadata.setCreator(fullNameFromUser());
        imageMetadata.setCopyright("Copyright " + Calendar.getInstance().get(Calendar.YEAR));

        return createImage(imageMetadata, inputStream);
    }

    private String fullNameFromUser() {
        User user = securityUtil.getCurrentUser();
        return user.getFirstName() + " " + user.getLastName();
    }

    public Photo createImage(ImageMetadata imageMetadata, InputStream inputStream) throws IllegalStateException, IOException {
        Photo photo = createImage(imageMetadata);

        imageManifestationService.createManifestationsFromOriginal(photo, inputStream);

        return photo;
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
        photo.setRoll(findNamedOrDefaultRoll(imageData.getRollName(), currentUser));

        imageInitializer.saveNewImage(photo);

        addImageToGroup(photo, photo.getRoll());

        return photo;
    }

    public ImageGroup findNamedOrDefaultRoll(String rollName, User owner) {
        if (StringUtils.isBlank(rollName)) {
            return findOrCreateDefaultRollForUser(owner);
        } else {
            ImageGroup roll = imageGroupRepository.getRollByOwnerAndName(owner, rollName);
            if (roll == null) {
                throw new ImageGroupNotFoundException("Roll not found: " + rollName);
            }
            return roll;
        }
    }

    private ImageGroup findOrCreateDefaultRollForUser(User currentUser) {
        ImageGroup defaultRoll = imageGroupRepository.getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME);
        if (defaultRoll == null) {
            defaultRoll = new ImageGroup(ImageGroup.DEFAULT_ROLL_NAME, currentUser, Type.ROLL);
            defaultRoll.setSupergroup(null);
            defaultRoll.setUploadDate(timeSource.today());
            defaultRoll.setDisplayName("Uploads");
            defaultRoll.setDescription("Uploaded Images");
            imageInitializer.saveNewImageGroup(defaultRoll);
        }
        return defaultRoll;
    }

    public ImageFrame addImageToGroup(Image image, ImageGroup group) {
        return addImageToGroup(image, group, imageGroupRepository.findMaxPosition(group) + 1);
    }

    public ImageFrame addImageToGroup(Image image, ImageGroup group, int position) {
        ImageFrame frame = new ImageFrame(image);
        frame.setPosition(position);
        frame.setImageGroup(group);
        group.addFrame(frame);

        imageGroupRepository.saveGroup(group);

        return frame;
    }

    public void makeImageGroupAndImagesPublic(ImageGroup group) {
        imageSecurityService.makePublic(group);
        log.info("Added public permission for group: " + group.getName());

        Collection<ImageFrame> frames = group.getFrames();

        for (ImageFrame frame : frames) {
            Image image = frame.getImage();
            imageSecurityService.makePublic(image);
            log.info("Added public permission for image: " + image.getDisplayName());
        }
    }

    public void makeImageGroupAndImagesPublic(String ownerName, Type type, String imageGroupName)
            throws NoSuchImageGroupException {
        User owner = userRepository.getByScreenName(ownerName);
        ImageGroup group = imageGroupRepository.getByOwnerNameAndTypeWithFrames(owner, imageGroupName, type);
        if (group == null) {
            log.error("No " + type + " found with name " + imageGroupName + " owned by " + owner);
        }
        makeImageGroupAndImagesPublic(group);
    }

    public void setImageGroupPreview(long imageGroupId, long imageId)
            throws NoSuchImageGroupException, NoSuchImageException {
        ImageGroup imageGroup = imageGroupRepository.loadById(imageGroupId);
        Image image = imageRepository.loadById(imageId);
        imageGroup.setPreviewImage(image);
        imageGroupRepository.saveGroup(imageGroup);
    }
}