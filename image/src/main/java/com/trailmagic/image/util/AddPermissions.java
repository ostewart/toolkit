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
package com.trailmagic.image.util;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.ImageService;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.image.NoSuchImageGroupException;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.user.Group;
import com.trailmagic.user.GroupFactory;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import com.trailmagic.user.UserLoginModule;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.acegisecurity.acl.basic.SimpleAclEntry;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class AddPermissions {
    private ImageRepository imageFactory;
    private ImageGroupRepository imageGroupRepository;
    private ImageSecurityService imageSecurityService;
    private UserFactory userFactory;
    private GroupFactory groupFactory;
    private HibernateTemplate hibernateTemplate;

    private static Logger s_log = Logger.getLogger(AddPermissions.class);

    private static final String EVERYONE_GROUP_NAME = "everyone";

    public void setImageGroupRepository(ImageGroupRepository imageGroupRepository) {
        this.imageGroupRepository = imageGroupRepository;
    }

    public void setImageSecurityService(ImageSecurityService imageSecurityService) {
        this.imageSecurityService = imageSecurityService;
    }

    public void setImageFactory(ImageRepository factory) {
        this.imageFactory = factory;
    }

    public void setUserFactory(UserFactory factory) {
        this.userFactory = factory;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setGroupFactory(GroupFactory groupFactory) {
        this.groupFactory = groupFactory;
    }

    public void makeEverythingPublic(final String ownerName) {
        hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    Collection<ImageGroup> imageGroups =
                        imageGroupRepository
                        .getAlbumsByOwnerScreenName(ownerName);
                    imageGroups.addAll(imageGroupRepository
                                       .getRollsByOwnerScreenName(ownerName));

                    for (ImageGroup group : imageGroups) {
                        makeGroupPublic(group);
                    }
                    return null;
                }
            });
    }

    // assumes within a session
    private void makeGroupPublic(ImageGroup group) {
        imageSecurityService.makePublic(group);
        s_log.info("Added public permission for group: "
                   + group.getName());

        Collection<ImageFrame> frames = group.getFrames();

        for (ImageFrame frame : frames) {
            imageSecurityService.makePublic(frame);
            s_log.info("Added public permission for frame: "
                       + frame.getPosition() + " of group "
                       + group.getName());

            Image image = frame.getImage();
            imageSecurityService.makePublic(image);
            s_log.info("Added public permission for image: "
                       + image.getDisplayName());

            for (ImageManifestation mf : image.getManifestations()) {
                imageSecurityService.makePublic(mf);
                s_log.info("Added public permission for "
                           + "manifestation: "
                           + mf.getHeight() + "x" + mf.getWidth());
            }
        }
    }

    public void makeImageGroupsPublic(final String ownerName, final String type,
                                      final Collection<String> groupNames) {

        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                User owner = userFactory.getByScreenName(ownerName);
                for (String groupName : groupNames) {
                    ImageGroup group;
                    if ("roll".equals(type)) {
                        group =
                            imageGroupRepository.getRollByOwnerAndName(owner,
                                                                      groupName);
                        if (group == null) {
                            s_log.error("No roll found with name " + groupName
                                        + " owned by " + owner);
                        }
                    } else if ("album".equals(type)) {
                        group =
                            imageGroupRepository.getAlbumByOwnerAndName(owner,
                                                                       groupName);
                        s_log.error("No album found with name " + groupName
                                    + " owned by " + owner);
                    } else {
                        throw new IllegalArgumentException("invalid type");
                    }
                    makeGroupPublic(group);
                }
                return null;
            }
        });
    }

    public void addAllOwnerPermissions(final String ownerName) {
        hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    List<ImageGroup> groups =
                        imageGroupRepository
                        .getRollsByOwnerScreenName(ownerName);
                    groups.addAll(imageGroupRepository
                                  .getAlbumsByOwnerScreenName(ownerName));
                    for (ImageGroup group : groups) {
                        imageSecurityService.addOwnerAcl(group);
                        for (ImageFrame frame : group.getFrames()) {
                            imageSecurityService.addOwnerAcl(frame);
                            Image image = frame.getImage();
                            imageSecurityService.addOwnerAcl(image);
                            for (ImageManifestation mf
                                     : image.getManifestations()) {
                                imageSecurityService.addOwnerAcl(mf);
                            }
                        }
                    }
                    return null;
                }
            });
    }

    public void addOwnerPermissions(final String ownerName,
				    final String albumName) {
        hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    User user = userFactory.getByScreenName(ownerName);
		    ImageGroup group =
			imageGroupRepository.getAlbumByOwnerAndName(user,
								   albumName);
		    imageSecurityService.addOwnerAcl(group);
		    for (ImageFrame frame : group.getFrames()) {
			imageSecurityService.addOwnerAcl(frame);
			Image image = frame.getImage();
			imageSecurityService.addOwnerAcl(image);
			for (ImageManifestation mf
				 : image.getManifestations()) {
			    imageSecurityService.addOwnerAcl(mf);
			}
		    }
		    return null;
		}
	    });
    }

    public void fixFramePermissions() {
        hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    List<ImageGroup> groups =
                        imageGroupRepository.getAll();

                    for (ImageGroup group : groups) {
                        for (ImageFrame frame : group.getFrames()) {
                            if (imageSecurityService.isPublic(frame
                                                                .getImage())) {
                                s_log.info("Making frame public: " + frame);
                                imageSecurityService.makePublic(frame);
                            } else {
                                s_log.info("Making frame private: " + frame);
                                imageSecurityService.makePrivate(frame);
                            }
                        }
                    }
                    return null;
                }
            });
    }

    public void makeAdminReadable(final String username,
                                  final Collection<Long> imageIds) {
        hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    User user = userFactory.getByScreenName(username);
                    for (Long id : imageIds) {
                        Image image = imageFactory.getById(id);
                        ImageGroup roll =
                            imageGroupRepository.getRollForImage(image);
                        imageSecurityService
                            .addPermission(image, roll, user,
                                           SimpleAclEntry.READ
                                           | SimpleAclEntry.ADMINISTRATION);
                    }
                    return null;
                }
            });
    }

    public void makeReadable(final String username,
                             final Collection<Long> imageIds) {
        hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    User user = userFactory.getByScreenName(username);
                    for (Long id : imageIds) {
                        Image image = imageFactory.getById(id);
                        ImageGroup roll =
                            imageGroupRepository.getRollForImage(image);
                        imageSecurityService
                            .addPermission(image, roll, user,
                                           SimpleAclEntry.READ);
                    }
                    return null;
                }
            });
    }

    public void addUserAccount(final String username,
                               final String password,
                               final String firstname,
                               final String lastname,
                               final String primaryEmail) {
        hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    try {
                        s_log.info("Adding account: " + username);
                        User user = new User();
                        user.setScreenName(username);
                        user.setFirstName(firstname);
                        user.setLastName(lastname);
                        user.setPrimaryEmail(primaryEmail);
                        user.setPassword(UserLoginModule
                                         .encodePassword(password.toCharArray()));
                        session.saveOrUpdate(user);
                        Group everyoneGroup =
                            groupFactory.getByName(EVERYONE_GROUP_NAME);
                        everyoneGroup.addUser(user);
                    } catch (Exception e) {
                        s_log.error("Error adding account", e);
                    }
                    return null;
                }
            });
    }

    public void addAccounts(final String filename) {
        try {
            final BufferedReader in =
                new BufferedReader(new FileReader(filename));
            for (String input = in.readLine();
                 input != null;
                 input = in.readLine()) {
                StringTokenizer st = new StringTokenizer(input,",");
                try {
                    String sname = st.nextToken();
                    addUserAccount(sname,
                                   st.nextToken(),
                                   st.nextToken(),
                                   st.nextToken(),
                                   st.nextToken());
                    s_log.info("Added account: " + sname);
                } catch (NoSuchElementException e) {
                    s_log.error("Invalid row:" + input);
                }
            }
            s_log.info("Finished reading input.");
        } catch (IOException e) {
            s_log.error("Error processing input", e);
        }
    }

    private static void printUsage() {
        System.err.println("Usage: AddPermissions <command> <owner> [<roll-name>]");
    }

    public static final void main(String[] args) {
        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-global.xml",
                 "applicationContext-user.xml",
                 "applicationContext-imagestore.xml",
                 "applicationContext-imagestore-authorization.xml",
                 "applicationContext-standalone.xml"});

        // can't proxy becuase it's a class :(
        // refactoring to ImageService
        AddPermissions ap = null;
//            (AddPermissions) appContext.getBean(ADD_PERMISSIONS_BEAN);

        String command = args[0];
        if ("fixFramePermissions".equals(command)) {
            ap.fixFramePermissions();
        } else if ("addAllOwnerPermissions".equals(command)) {
            ap.addAllOwnerPermissions(args[1]);
        } else if ("addOwnerPermissions".equals(command)) {
            ap.addOwnerPermissions(args[1], args[2]);
        } else if ("addUserAccount".equals(command)) {
            ap.addUserAccount(args[1], args[2], args[3], args[4], args[5]);
        } else if ("makeAdminReadable".equals(command)) {
            ArrayList<Long> imageIds = new ArrayList<Long>();
            for (int i = 2; i < args.length; i++) {
                imageIds.add(new Long(args[i]));
            }
            ap.makeAdminReadable(args[1], imageIds);
        } else if ("makeReadableBy".equals(command)) {
            ArrayList<Long> imageIds = new ArrayList<Long>();
            for (int i = 2; i < args.length; i++) {
                imageIds.add(new Long(args[i]));
            }
            ap.makeReadable(args[1], imageIds);
        } else if ("addAccounts".equals(command)) {
            ap.addAccounts(args[1]);
        } else if ("makeImageGroupsPublic".equals(command)) {
            ImageService manager =
                (ImageService) appContext.getBean("imageService");
            for (int i = 3; i < args.length; i++) {
                try {
                    manager.makeImageGroupPublic(args[1],
                                                 ImageGroup.Type.fromString(args[2]),
                                                 args[i]);
                } catch (NoSuchImageGroupException e) {
                    s_log.error("Couldn't make group public:", e);
                }
            }
            // username, type, groups...
//            ap.makeImageGroupsPublic(args[1], args[2], groups);
        } else if ("makeEverythingPublic".equals(command)) {
            ap.makeEverythingPublic(args[1]);
        } else {
            printUsage();
            System.exit(1);
        }
    }
}
