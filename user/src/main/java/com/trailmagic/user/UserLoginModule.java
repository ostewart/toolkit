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
package com.trailmagic.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


/**
 * Some code from Hibernate example: http://www.hibernate.org/139.html
 **/

public class UserLoginModule implements LoginModule {
    private Subject m_subject;
    private CallbackHandler m_handler;
    private SessionFactory m_sessFactory;
    private List<Principal> m_principals;
    private boolean m_success;
    private static Logger s_log = Logger.getLogger(UserLoginModule.class);

    private static final String USER_BY_SN_QUERY = "userByScreenName";
    private static final String GROUPS_QUERY = "groupsForUserId";
    private static final String HASH_ALGORITHM = "MD5";
    private static final String CFG_FILE = "/trailmagic-user.cfg.xml";

    public void initialize(Subject subject, CallbackHandler handler,
                           Map sharedState, Map options) {
        m_handler = handler;
        m_subject = subject;
        m_principals = new ArrayList<Principal>();
        m_success = false;

        try {
            m_sessFactory =
                new Configuration().configure(CFG_FILE).buildSessionFactory();
        } catch (HibernateException ex) {
            s_log.error("Couldn't initialize", ex);
            m_sessFactory = null;
        }
    }

    public boolean login() throws LoginException {
        s_log.debug("UserLoginModule.login called!!");
        if (m_handler == null) {
            throw new LoginException("Error: no CallbackHandler available");
        }

        try {
            Callback[] callbacks = new Callback[] {
                new NameCallback("User: "),
                new PasswordCallback("Password: ", false)
            };

            m_handler.handle(callbacks);

            String username = ((NameCallback)callbacks[0]).getName();
            char[] password = ((PasswordCallback)callbacks[1]).getPassword();

            ((PasswordCallback)callbacks[1]).clearPassword();

            // do app-specific validation
            m_success = validate(username, password);

            callbacks[0] = null;
            callbacks[1] = null;

            return m_success;
        } catch (Exception e) {
            s_log.error("Error processing login", e);
            throw new LoginException(e.getMessage());
        }
    }

    public boolean logout() throws LoginException {
        m_principals.clear();

        // remove the principals the login module added
      Iterator iter =
          m_subject.getPrincipals(UserPrincipal.class).iterator();
      while (iter.hasNext()) {
         UserPrincipal prin = (UserPrincipal)iter.next();
         m_subject.getPrincipals().remove(prin);
      }

      iter = m_subject.getPrincipals(GroupPrincipal.class).iterator();
      while (iter.hasNext()) {
          GroupPrincipal prin = (GroupPrincipal)iter.next();
          m_subject.getPrincipals().remove(prin);
      }

      return true;
    }

    public boolean abort() throws LoginException {
        m_success = false;
        return logout();
    }

    public boolean commit() throws LoginException {
        s_log.debug("commit() called!");
        if (m_success) {
            if (m_subject.isReadOnly()) {
                throw new LoginException("Subject is read-only");
            }
            try {
                m_subject.getPrincipals().addAll(m_principals);
                m_principals.clear();
            } catch (Exception e) {
                throw new LoginException(e.getMessage());
            }
        } else {
            m_principals.clear();
        }
        s_log.debug("returning true!");
        return true;
    }

    public static String encodePassword(char[] password)
        throws RuntimeException {

        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] passBytes = new byte[password.length];
            for (int i=0; i < passBytes.length; i++) {
                passBytes[i] = (byte) password[i];
            }
            return new String(Hex.encodeHex(md.digest(passBytes)));
        } catch (NoSuchAlgorithmException e) {
            s_log.error("Digest algorithm not found: " + HASH_ALGORITHM);
            throw new RuntimeException(e);
        }
    }

    private boolean validate(String username, char[] password)
        throws Exception {

        boolean valid = false;
        User user = null;
        Session sess = null;

        try {
            sess = m_sessFactory.openSession();

            // validate with the User from hibernate
            Query query =
                sess.getNamedQuery(USER_BY_SN_QUERY);
            query.setString("screenName", username);

            //            user = (User)query.uniqueResult();
            Object obj = query.uniqueResult();
            //            System.err.println("uniqueResult returned a " + obj.getClass());
            user = (User)obj;
            if ( user == null ) {
                return false;
            }

            String storedPass = user.getPassword();

            if ( storedPass != null && password != null &&
                 password.length > 0 ) {

                String digest = encodePassword(password);
                valid = storedPass.equals(digest);
                //                s_log.debug("digest = " + digest);
            }

            if (valid) {
                m_principals.add(new UserPrincipal(user.getScreenName()));
                query = sess.getNamedQuery(GROUPS_QUERY);
                query.setLong("userId", user.getId());
                Iterator iter = query.iterate();
                Collection<GroupPrincipal> groups =
                    new ArrayList<GroupPrincipal>();
                while (iter.hasNext()) {
                    groups.add(new GroupPrincipal(((Group)iter.next())
                                                  .getName()));
                }
                m_principals.addAll(groups);
            }
        } catch (HibernateException e) {
            s_log.error("Hibernate error validating credentials", e);
        } finally {
            if (sess != null) {
                try {
                    sess.close();
                } catch (HibernateException e) {
                    s_log.error("Error closing session", e);
                }
            }
        }

        return valid;
    }
}
