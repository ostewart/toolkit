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
package com.trailmagic.image.hibernate;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroup.Type;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.NoSuchImageFrameException;
import com.trailmagic.image.NoSuchImageGroupException;
import com.trailmagic.user.User;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
// for query.list()
@Repository("imageGroupRepository")
public class HibernateImageGroupRepository implements ImageGroupRepository {
    private static final String IMGFRAME_BY_GROUP_NAME_TYPE_AND_IMAGE_ID_QRY =
            "imageFrameByGroupNameTypeAndImageId";
    private static final String GROUP_OWNERS_QRY =
            "groupOwnersByType";
    private static final String GROUPS_BY_OWNER_NAME_QRY =
            "groupsByOwnerScreenNameAndType";
    private static final String GROUP_BY_OWNER_NAME_TYPE_QRY =
            "groupByOwnerGroupNameAndType";
    private static final String ROLL_BY_OWNER_AND_NAME_QRY =
            "rollByOwnerAndName";
    private static final String GROUPS_BY_IMAGE_QRY =
            "groupsByImage";
    private static final String ROLL_FOR_IMAGE_QRY =
            "rollForImage";
    private static final String ALL_GROUPS_QUERY =
            "allImageGroups";

    private SessionFactory sessionFactory;
    private HibernateTemplate hibernateTemplate;

    @Autowired
    public HibernateImageGroupRepository(SessionFactory sessionFactory, HibernateTemplate hibernateTemplate) {
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = hibernateTemplate;
    }


    public ImageGroup getRollByOwnerAndName(User owner, String rollName) {
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory, false);
            Query qry = session.getNamedQuery(ROLL_BY_OWNER_AND_NAME_QRY);
            qry.setEntity("owner", owner);
            qry.setString("rollName", rollName);
            qry.setCacheable(true);
            return (ImageGroup) qry.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageFrame getImageFrameByGroupNameTypeAndImageId(String groupName, Type groupType, long imageId)
            throws NoSuchImageFrameException {
        List results = hibernateTemplate.findByNamedQueryAndNamedParam(IMGFRAME_BY_GROUP_NAME_TYPE_AND_IMAGE_ID_QRY,
                                                                       new String[]{"groupName", "groupType", "imageId"},
                                                                       new Object[]{groupName, groupType, imageId});
        if (results.size() == 0) {
            throw new NoSuchImageFrameException(groupName, imageId);
        } else {
            // XXX: handle > 1 results

            // XXX: this seems pretty lame
            ImageFrame frame = (ImageFrame) results.get(0);
            frame.getImageGroup().getFrames().first();
            return frame;
        }
    }

    public List<User> getOwnersByType(Type groupType) {
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory, false);
            Query qry = session.getNamedQuery(GROUP_OWNERS_QRY);
            qry.setString("groupType", groupType.toString());
            qry.setCacheable(true);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<ImageGroup> getByOwnerScreenNameAndType(String screenName, Type groupType) {
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory, false);
            Query qry = session.getNamedQuery(GROUPS_BY_OWNER_NAME_QRY);
            qry.setString("screenName", screenName);
            qry.setString("groupType", groupType.toString());
            qry.setCacheable(true);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageGroup getByOwnerNameAndTypeWithFrames(User owner, String groupName, Type groupType) {
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory, false);
            Query qry = session.getNamedQuery(GROUP_BY_OWNER_NAME_TYPE_QRY);
            qry.setEntity("owner", owner);
            qry.setString("groupName", groupName);
            qry.setString("groupType", groupType.toString());
            qry.setCacheable(true);
            final ImageGroup group = (ImageGroup) qry.uniqueResult();
            // join fetch stopped working after enabling caching?
            if (group != null && group.getFrames().size() > 0) {
                group.getFrames().first();
            }
            return group;
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<ImageGroup> getByImage(Image image) {
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory, false);
            Query qry = session.getNamedQuery(GROUPS_BY_IMAGE_QRY);
            qry.setEntity("image", image);
            qry.setCacheable(true);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageGroup getRollForImage(Image image) {
        // images should always only be in one roll
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory, false);
            Query qry = session.getNamedQuery(ROLL_FOR_IMAGE_QRY);
            qry.setEntity("image", image);
            qry.setCacheable(true);
            List results = qry.list();
            if (results.size() < 1) {
                return null;
            }

            return (ImageGroup) results.get(0);
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    @Secured("AFTER_ACL_READ")
    public ImageGroup getById(long id) {
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory, false);

            return (ImageGroup) session.get(ImageGroup.class, id);
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageGroup getByIdWithFrames(long id) {
        ImageGroup imageGroup = getById(id);
        imageGroup.getFrames().first();
        return imageGroup;
    }

    public ImageGroup loadById(long imageGroupId) throws NoSuchImageGroupException {
        try {
            return hibernateTemplate.load(ImageGroup.class, imageGroupId);
        } catch (ObjectRetrievalFailureException e) {
            throw new NoSuchImageGroupException(imageGroupId, e);
        }
    }

    public List<ImageGroup> getAll() {
        return (List<ImageGroup>)
                hibernateTemplate.execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        Query qry = session.getNamedQuery(ALL_GROUPS_QUERY);
                        return qry.list();
                    }
                });
    }

    @Transactional(readOnly = false)
    public void saveNewGroup(ImageGroup newGroup) {
        hibernateTemplate.save(newGroup);
    }

    @Transactional(readOnly = false)
    public ImageGroup saveGroup(ImageGroup imageGroup) {
        return hibernateTemplate.merge(imageGroup);
    }

    public int getPublicFrameCount(ImageGroup group) {
        return (Integer) hibernateTemplate.findByNamedQuery("publicFrameCount", group.getId()).get(0);

    }

}
