package com.trailmagic.image.ui;

import com.trailmagic.image.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;


public class DisplayServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        Collection errors = new ArrayList();
        try {
            String pathInfo = req.getPathInfo();
            if ( pathInfo == null ) {
                processList(req, res);
                return;
            }
            StringTokenizer args = new StringTokenizer(pathInfo, "/");

            if ( !args.hasMoreTokens() ) {
                processList(req, res);
                return;
            }
            String method = args.nextToken();
            if ( method.equals("by-id") ) {
                Session session = HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                
                Query query =
                    session.createQuery("select from com.trailmagic.image.Image as image where image.id = :id");
                query.setLong("id", Long.parseLong(args.nextToken()));
                
                Image image = (Image)query.uniqueResult();
                if ( image != null ) {
                    req.setAttribute("image", image);
                    req.setAttribute("isPhoto",
                                     new Boolean(image instanceof Photo));
                    req.getRequestDispatcher("/display.jsp").include(req, res);
                    tx.commit();
                    HibernateUtil.closeSession();
                    return;
                } else {
                    errors.add("Image query returned no results.");
                    req.setAttribute("errors", errors);
                    req.getRequestDispatcher("/display-error.jsp")
                        .forward(req, res);
                    tx.commit();
                    HibernateUtil.closeSession();
                    return;
                }
                
                
            } else {
                errors.add("Method " + method + " not recognized.");
            }
        } catch (NullPointerException e) {
            errors.add("No image with the specified ID.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/display-error.jsp").forward(req, res);
            return;
        } catch (NoSuchElementException e) {
            errors.add("No image specified.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/display-error.jsp").forward(req, res);
            return;
        } catch (HibernateException e) {
            errors.add("Error retrieving image:");
            errors.add(e.getMessage());
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/display-error.jsp").forward(req, res);
            return;
        }
        errors.add("No image specified.");
        req.setAttribute("errors", errors);
        req.getRequestDispatcher("/display-error.jsp").forward(req, res);
    }

    private void processList(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        try {
            Session session = HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            Query query =
                session.createQuery("from com.trailmagic.image.Image");
            List images = query.list();
            req.setAttribute("images", images);
            req.getRequestDispatcher("/list.jsp").forward(req, res);
            return;
        } catch (HibernateException e) {
            Collection errors = new ArrayList();
            errors.add("Error retrieving image list.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/display-error.jsp").forward(req, res);
        }
    }
}
