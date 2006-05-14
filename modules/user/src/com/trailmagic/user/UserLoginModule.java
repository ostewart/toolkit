package com.trailmagic.user;

import java.security.MessageDigest;
import java.security.Principal;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.CallbackHandler;
import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import com.trailmagic.util.tomcat.HexUtils;



/**
 * Some code from Hibernate example: http://www.hibernate.org/139.html
 **/

public class UserLoginModule implements LoginModule {
    private Subject m_subject;
    private CallbackHandler m_handler;
    private SessionFactory m_sessFactory;
    private List<Principal> m_principals;
    private boolean m_success;

    private static final String USER_QUERY =
        "select from com.trailmagic.user.User as user " +
        "where user.screenName = :screenName";
    private static final String GROUPS_QUERY =
        "select grp from com.trailmagic.user.Group as grp " +
        "inner join grp.users as user " +
        "where user.id = :id";
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
            ex.printStackTrace();
            m_sessFactory = null;
        }
    }

    public boolean login() throws LoginException {
        System.err.println("UserLoginModule.login called!!");
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
            e.printStackTrace();
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
        System.err.println("commit() called!");
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
        System.err.println("returning true!");
        return true;
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
                sess.createQuery(USER_QUERY);
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

                MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
                // this seems a little sketchy...are we handling charset right?
                // what's the result when someone uses a non-ascii char?
                String digest =
                    HexUtils.convert(md.digest((new String(password))
                                               .getBytes()));
                valid = storedPass.equals(digest);
                System.err.println("digest = " + digest);
            }

            if (valid) {
                m_principals.add(new UserPrincipal(user.getScreenName()));
                query = sess.createQuery(GROUPS_QUERY);
                query.setLong("id", user.getId());
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
            // ignore
            e.printStackTrace();
        } finally {
            if (sess != null) {
                try {
                    sess.close();
                } catch (HibernateException e) {
                    // ignore
                    e.printStackTrace();
                }
            }
        }

        return valid;
    }
}
