package com.trailmagic.image.ui;

import com.trailmagic.image.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Query;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import java.sql.Blob;
import java.sql.SQLException;

public class ImageMFServlet extends HttpServlet {
    private static final String IMAGE_ID_PARAM = "image_id";
    private static final String MF_ID_PARAM = "mfid";
    private static final String MF_JSP = "/manifestation.jsp";
    private static final String IMAGE_QUERY =
        "SELECT FROM com.trailmagic.image.Image AS image WHERE image.id = :id";
    private static final String MF_QUERY =
        "SELECT FROM com.trailmagic.image.ImageManifestation AS imf" +
        " WHERE imf.id = :id";

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        req.setAttribute("IMAGE_ID_PARAM", IMAGE_ID_PARAM);
        req.setAttribute("MF_ID_PARAM", MF_ID_PARAM);
        

        Collection errors = new ArrayList();
        Query query = null;
        Session session = null;
        Transaction tx = null;

        if (req.getParameter(IMAGE_ID_PARAM) == null ) {
            errors.add("No Image Specified.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(MF_JSP).forward(req, res);
            return;
        }
        
        try {
            if ( req.getParameter(IMAGE_ID_PARAM) != null ) {
                long imageId =
                    Long.parseLong(req.getParameter(IMAGE_ID_PARAM));
                session = HibernateUtil.currentSession();
                //                tx = session.beginTransaction();
                
                query =
                    session.createQuery(IMAGE_QUERY);
                query.setLong("id", imageId);
                
                Image image = (Image)query.uniqueResult();
                if ( image != null ) {
                    req.setAttribute("image", image);
                    req.setAttribute("isPhoto",
                                     new Boolean(image instanceof Photo));
                    req.getRequestDispatcher(MF_JSP).include(req, res);
                    HibernateUtil.closeSession();
                    return;
                } else {
                    errors.add("Image query returned no results.");
                    req.setAttribute("errors", errors);
                    req.getRequestDispatcher(MF_JSP).forward(req, res);
                    HibernateUtil.closeSession();
                    return;
                }
            } else if ( req.getParameter(MF_ID_PARAM) != null ) {
                session = HibernateUtil.currentSession();
                long mfId = Long.parseLong(req.getParameter(MF_ID_PARAM));
                query = session.createQuery(MF_QUERY);
                query.setLong("id", mfId);
                ImageManifestation mf = (ImageManifestation)query.uniqueResult();
                if ( mf != null ) {
                    req.setAttribute("mf", mf);
                    req.setAttribute("image", mf.getImage());
                    req.getRequestDispatcher(MF_JSP).include(req, res);
                    HibernateUtil.closeSession();
                    return;
                } else {
                    errors.add("Image Manifestation query returned no results.");
                    req.setAttribute("errors", errors);
                    req.getRequestDispatcher(MF_JSP).include(req, res);
                    HibernateUtil.closeSession();
                    return;
                }
            }
        } catch (NumberFormatException e) {
            errors.add("Invalid ID specified.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(MF_JSP).forward(req, res);
            return;
        } catch (HibernateException e) {
            errors.add("Persistence error:");
            errors.add(e.getMessage());
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(MF_JSP).forward(req, res);
            return;
        }

        
        req.getRequestDispatcher(MF_JSP).forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        Collection errors = new ArrayList();

        if (!DiskFileUpload.isMultipartContent(req)) {
            errors.add("Invalid form encoding!");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(MF_JSP).forward(req, res);
            return;
        }

        DiskFileUpload upload = new DiskFileUpload();
        upload.setSizeMax(100 * 1024 * 1024); // 100MB
        Session sess = null;
        Transaction tx = null;

        try {
            List items = upload.parseRequest(req);
            Iterator iter = items.iterator();
            FileItem item = null;
            String name = null;
            boolean originalp = false;
            ImageManifestation im = null;
            Long imageId = null;

            sess = HibernateUtil.currentSession();
            tx = sess.beginTransaction();
            
            while ( iter.hasNext() ) {
                item = (FileItem)iter.next();
                if (item.isFormField()) {
                    if ("name".equals(item.getFieldName())) {
                        name = item.getString();
                    } else if ("image_id".equals(item.getFieldName())) {
                        imageId = new Long(item.getString());
                    } else if ("original".equals(item.getFieldName())) {
                        originalp =
                            item.getString().toLowerCase().equals("true");
                    }
                } else {
                    im = processFile(item);
                }
            }

            if ( im == null ) {
                errors.add("Please select a file to upload.");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher(MF_JSP).forward(req, res);
                return;
            }

            if ( imageId == null ) {
                errors.add("Error: no image specified.");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher(MF_JSP).forward(req, res);
                return;
            } else {
                Image image = Image.findById(imageId.longValue());
                if ( image == null ) {
                    errors.add("Invalid image specified.");
                    req.setAttribute("errors", errors);
                    req.getRequestDispatcher(MF_JSP).forward(req, res);
                    return;
                }
                //                im.setImage(image);
                image.addManifestation(im);
                im.setOriginal(originalp);
                im.setHeight(0);
                im.setWidth(0);
            }
                
            if ( name != null ) {
                im.setName(name);
            }
            sess.saveOrUpdate(im);
            tx.commit();
            //            errors.add("new IM: " + im.getId());
            HibernateUtil.closeSession();
            
        } catch (FileUploadException e) {
            errors.add("Error in upload: " + e.getMessage());
        } catch (NumberFormatException e) {
            errors.add("Invalid image ID.");
        } catch (HibernateException e) {
            if ( tx != null ) {
                try {
                    tx.rollback();
                } catch (HibernateException e2) {
                    errors.add("Error rolling back transaction!");
                }
            }
            errors.add("Persistence error: " + e.getMessage());
        } catch (SQLException e) {
            errors.add("Error saving image: " + e.getMessage());
        }

        req.setAttribute("errors", errors);
        req.getRequestDispatcher(MF_JSP).forward(req, res);
    }

    private ImageManifestation processFile(FileItem item)
        throws IOException, SQLException {

        HeavyImageManifestation im = new HeavyImageManifestation();
        im.setData(Hibernate.createBlob(item.getInputStream()));
        //        im.setData(item.get());
        im.setName(item.getName());
        im.setFormat(item.getContentType());
        return im;
    }
}
