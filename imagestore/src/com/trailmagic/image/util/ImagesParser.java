package com.trailmagic.image.util;

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.HibernateException;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Stack;

import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import com.trailmagic.image.*;

public class ImagesParser extends DefaultHandler
    implements ApplicationContextAware {

    private static final String USER_FACTORY_BEAN = "userFactory";

    private String m_inElement;
    private String m_inSubElement;
    private Stack m_context;
    private Image m_image;
    private ImageGroup m_roll;
    private Session m_session;
    private SessionFactory m_sessionFactory;
    private Transaction m_transaction;
    private boolean m_inImage;
    private boolean m_inRoll;

    private ApplicationContext m_appContext;


    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        m_appContext = applicationContext;
    }

    public void startDocument() {
        try {
            m_session = m_sessionFactory.openSession();
            m_transaction = m_session.beginTransaction();
            m_inImage = false;
            m_inRoll = false;
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public void endDocument() {
        try {
            m_transaction.commit();
            m_session.close();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    private void abort() {
        try {
            m_transaction.rollback();
            m_session.close();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    public void fatalError(SAXParseException e) throws SAXException {
        abort();
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {
        String eltName = qName;
        m_context.push(qName);


        if ("image".equals(eltName)) {
            startImage();
        } else if ("roll".equals(eltName)) {
            startRoll();
        }
        /*
        if ( eltName.equals("images") ) {
            //            startImages();
        } else if ( eltName.equals("image") ) {
            startImage();
        } else if ( eltName.equals("name") ) {
            //            startName();
        } else if ( eltName.equals("display-name") ) {
            //            startDisplayName();
        } else if ( eltName.equals("caption") ) {
            //            startCaption();
        } else if ( eltName.equals("copyright") ) {
            //            startCopyright();
        } else if ( eltName.equals("creator") ) {
            //            startCreator();
        } else if ( eltName.equals("owner") ) {
            //            startOwner();
        } else if ( eltName.equals("photo-data") ) {
            //            startPhotoData();
        } else if ( eltName.equals("image-manifestation") ) {
            //            startImageManifestation();
        } else if ( eltName.equals("notes") ) {
            //            startNotes();
        } else if ( eltName.equals("height") ) {
            //            startHeight();
        } else if ( eltName.equals("width") ) {
            //            startWidth();
        } else if ( eltName.equals("original") ) {
            //            startOriginal();
        } else if ( eltName.equals("filename") ) {
            //            startFilename();
        } else if ( eltName.equals("roll") ) {
            //            startRoll();
        }
        */
    }

    public void endElement(String uri, String localName, String qName,
                           Attributes attributes) {
        
        m_context.pop();
        String eltName = qName;

        if ("image".equals(eltName)) {
            endImage();
        } else if ("roll".equals(eltName)) {
            endRoll();
        }
    }

    public void startImage() {
        m_image = new Image();
        m_inImage = true;
    }

    public void endImage() {
        try {
            m_session.save(m_image);
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
        m_image = null;
        m_inImage = false;
    }

    public void startRoll() {
        m_roll = new ImageGroup();
        m_inRoll = true;
    }


    public void endRoll() {
        try {
            m_session.save(m_roll);
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
        m_roll = null;
        m_inRoll = false;
    }

    public void characters(char ch[], int start, int length) {
        String currentElt = (String)m_context.peek();
        if (m_inImage) {
            if ( "name".equals(currentElt) ) {
                m_image.setName(new String(ch, start, length));
            } else if ( "display-name".equals(currentElt) ) {
                m_image.setDisplayName(new String(ch, start, length));
            } else if ( "caption".equals(currentElt) ) {
                m_image.setCaption(new String(ch, start, length));
            } else if ( "copyright".equals(currentElt) ) {
                m_image.setCopyright(new String(ch, start, length));
            } else if ( "creator".equals(currentElt) ) {
                m_image.setCreator(new String(ch, start, length));
            } else if ( "owner".equals(currentElt) ) {
                String ownerName = new String(ch, start, length);
                UserFactory uf =
                    (UserFactory)m_appContext.getBean(USER_FACTORY_BEAN);
                m_image.setOwner(uf.getByScreenName(ownerName));
            } else if ( "notes".equals(currentElt) ) {
                m_image.setName(new String(ch, start, length));
            }
        }
        if (m_inRoll) {
            if ( "name".equals(currentElt) ) {
                m_roll.setName(new String(ch, start, length));
            } else if ( "display-name".equals(currentElt) ) {
                m_roll.setDisplayName(new String(ch, start, length));
            } else if ( "description".equals(currentElt) ) {
                m_roll.setDescription(new String(ch, start, length));
            } else if ( "owner".equals(currentElt) ) {
                m_roll.setName(new String(ch, start, length));
                String ownerName = new String(ch, start, length);
                UserFactory uf =
                    (UserFactory)m_appContext.getBean(USER_FACTORY_BEAN);
                m_roll.setOwner(uf.getByScreenName(ownerName));
            }
        }
    }

    public static final void main(String[] args) {
        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext.xml"});
        ImagesParser handler = new ImagesParser();
        handler.setApplicationContext(appContext);

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(args[0], handler);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

}
