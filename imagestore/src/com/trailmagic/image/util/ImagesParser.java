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
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import com.trailmagic.image.*;

public class ImagesParser extends DefaultHandler
    implements ApplicationContextAware {

    private static final String USER_FACTORY_BEAN = "userFactory";
    private static final String METADATA_FILENAME = "image-data.xml";

    private String m_inElement;
    private String m_inSubElement;
    private Stack m_context;
    private Image m_image;
    private ImageGroup m_roll;
    private ImageManifestation m_manifestation;
    private Session m_session;
    private SessionFactory m_sessionFactory;
    private Transaction m_transaction;
    private boolean m_inImage;
    private boolean m_inRoll;
    private boolean m_inManifestation;
    private boolean m_closeSession;
    private File m_baseDir;

    private ApplicationContext m_appContext;

    public ImagesParser() {
        this(true);
    }

    public ImagesParser(boolean closeSession) {
        m_context = new Stack();
        m_closeSession = closeSession;
    }

    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        m_appContext = applicationContext;
    }

    public File getBaseDir() {
        return m_baseDir;
    }

    public void setBaseDir(File baseDir) {
        m_baseDir = baseDir;
    }

    public void startDocument() {
        try {
            m_session = m_sessionFactory.openSession();
            m_transaction = m_session.beginTransaction();
            m_inImage = false;
            m_inRoll = false;
            m_inManifestation = false;
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public void endDocument() {
        try {
            m_transaction.commit();
            System.err.println("ImagesParser: committed transaction.");
            if ( m_closeSession ) {
                m_session.close();
            }
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    private void abort() {
        try {
            m_transaction.rollback();
            if (m_closeSession) {
                m_session.close();
            }
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
        m_context.push(eltName);


        if ("image".equals(eltName)) {
            startImage();
        } else if ("roll".equals(eltName)) {
            startRoll();
        } else if ("image-manifestation".equals(eltName)) {
            startManifestation();
        }
    }

    public void endElement(String uri, String localName, String qName) {
        m_context.pop();
        String eltName = qName;

        if ("image".equals(eltName)) {
            endImage();
        } else if ("roll".equals(eltName)) {
            endRoll();
        } else if ("image-manifestation".equals(eltName)) {
            endManifestation();
        }
    }

    public void startImage() {
        m_image = new Image();
        m_inImage = true;
    }

    public void endImage() {
        try {
            System.out.println("endImage() called");
            m_session.save(m_image);
            System.out.println("Image saved: " + m_image.getName() + " ("
                               + m_image.getId() + ")");
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
        m_image = null;
        m_inImage = false;
    }

    public void startManifestation() {
        m_manifestation = new ImageManifestation();
        m_manifestation.setImage(m_image);
        m_inManifestation = true;
    }

    public void endManifestation() {
        try {
            System.out.println("saving ImageManifestation: "
                               + "name: " + m_manifestation.getName()
                               + "height: " + m_manifestation.getHeight()
                               + "width: " + m_manifestation.getWidth()
                               + "format: " + m_manifestation.getFormat()
                               + "original: " + m_manifestation.isOriginal());

            importManifestation();
            m_session.save(m_manifestation);
            System.out.println("ImageManifestation saved: "
                               + m_manifestation.getName()
                               + " (" + m_manifestation.getId() + ")");
            m_manifestation = null;
            m_inManifestation = false;
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public void startRoll() {
        m_roll = new ImageGroup();
        m_roll.setType(ImageGroup.ROLL_TYPE);
        m_roll.setSupergroup(null);
        m_inRoll = true;
    }


    public void endRoll() {
        try {
            System.out.println("endRoll() called");
            m_session.save(m_roll);
            System.out.println("Roll saved: " + m_roll.getName() + " ("
                               + m_roll.getId() + ")");
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
        m_roll = null;
        m_inRoll = false;
    }

    public void characters(char ch[], int start, int length) {
        String currentElt = (String)m_context.peek();
        if (m_inImage) {
            if (!m_inManifestation) {
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
                } else if ( "number".equals(currentElt) ) {
                    m_image.setNumber(new Integer(new String(ch, start,
                                                             length)));
                }
            } else {
                if ("name".equals(currentElt)) {
                    m_manifestation.setName(new String(ch, start, length));
                } else if ("height".equals(currentElt)) {
                    m_manifestation.setHeight(Integer
                                              .parseInt(new String(ch,
                                                                   start,
                                                                   length)));
                } else if ("width".equals(currentElt)) {
                    m_manifestation.setWidth(Integer
                                             .parseInt(new String(ch,
                                                                  start,
                                                                  length)));
                } else if ("format".equals(currentElt)) {
                    m_manifestation.setFormat(new String(ch, start, length));
                } else if ("original".equals(currentElt)) {
                    m_manifestation.setOriginal(new Boolean(new String(ch,
                                                                       start,
                                                                       length))
                                                .booleanValue());
                }
            }
        } else if (m_inRoll) {
            if ( "name".equals(currentElt) ) {
                m_roll.setName(new String(ch, start, length));
            } else if ( "display-name".equals(currentElt) ) {
                m_roll.setDisplayName(new String(ch, start, length));
            } else if ( "description".equals(currentElt) ) {
                m_roll.setDescription(new String(ch, start, length));
            } else if ( "owner".equals(currentElt) ) {
                String ownerName = new String(ch, start, length);
                UserFactory uf =
                    (UserFactory)m_appContext.getBean(USER_FACTORY_BEAN);
                m_roll.setOwner(uf.getByScreenName(ownerName));
            }
        }
    }

    public static void printUsage() {
        System.err.println("Usage: ImagesParser <base_dir>");
    }

    public static void importManifestation(File baseDir,
                                           ImageManifestation mf) {
        try {
            File srcFile = new File(baseDir, mf.getName());
            System.out.print("Importing " + srcFile.getPath() + "...");
            if ( srcFile.length() > Integer.MAX_VALUE ) {
                System.out.println("File is too big...skipping.");
                return;
            }
            byte[] data = new byte[(int)srcFile.length()];
            FileInputStream fis = new FileInputStream(srcFile);
            fis.read(data);
            mf.setData(data);
            fis.close();
            System.out.println("done");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void importManifestation() {
        try {
            File srcFile = new File(m_baseDir, m_manifestation.getName());
            System.out.print("Importing " + srcFile.getPath() + "...");
            if ( srcFile.length() > Integer.MAX_VALUE ) {
                System.out.println("File is too big...skipping.");
                return;
            }
            byte[] data = new byte[(int)srcFile.length()];
            FileInputStream fis = new FileInputStream(srcFile);
            fis.read(data);
            m_manifestation.setData(data);
            fis.close();
            System.out.println("done");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static final void main(String[] args) {
        File baseDir = new File(args[0]);
        if (!baseDir.isDirectory()) {
            printUsage();
            System.exit(1);
        }

        File metadataFile = new File(baseDir, METADATA_FILENAME);
        if (!metadataFile.isFile()) {
            System.err.println("Error: Couldn't find " + baseDir
                               + File.separator
                               + METADATA_FILENAME);
            System.exit(1);
        }

        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-standalone.xml"});
        ImagesParser handler =
            (ImagesParser)appContext.getBean("imagesParser");
        //        handler.setApplicationContext(appContext);
        handler.setBaseDir(baseDir);
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();

            System.out.print("Parsing " + metadataFile.getPath() + "...");
            System.out.flush();
            parser.parse(metadataFile, handler);
            System.out.println("done");

            /*
            System.out.println("Importing image files:");
            List manifestations = handler.getManifestations();
            Iterator iter = manifestations.iterator();
            while (iter.hasNext()) {
                importManifestation(baseDir, (ImageManifestation)iter.next());
            }
            System.out.println("All done.");
            */
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

}
