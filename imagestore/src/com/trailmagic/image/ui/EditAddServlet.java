package com.trailmagic.image.ui;

import com.trailmagic.image.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import java.util.Collection;
import java.util.ArrayList;

public class EditAddServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        Collection errors = new ArrayList();
        String idParam = req.getParameter("id");
        if ( idParam != null ) {
            try {
                req.setAttribute("operation", "Edit");
                long id = Long.parseLong(idParam);

                Image image = Image.findById(id);
                req.setAttribute("image", image);
                req.setAttribute("errors", errors);
                req.getRequestDispatcher("/edit-add.jsp").include(req, res);
                HibernateUtil.closeSession();
            } catch (NumberFormatException e) {
                errors.add("Invalid ID Specified.");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher("/edit-add.jsp").forward(req, res);
            } catch (HibernateException e) {
                errors.add("Error retrieving Image: " + e.getMessage());
                req.setAttribute("errors", errors);
                req.getRequestDispatcher("/edit-add.jsp").forward(req, res);
            }
        } else {
            req.setAttribute("operation", "Add");
            // XXX: double-click protection
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/edit-add.jsp").forward(req, res);
        }

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        Collection errors = new ArrayList();
        Image image;
        String idParam = req.getParameter("id");
        Session sess;
        Transaction tx = null;

        String name = req.getParameter("name");
        String title = req.getParameter("title");
        String caption = req.getParameter("caption");
        String creator = req.getParameter("creator");
        String copyright = req.getParameter("copyright");
        try {
            if ( idParam != null ) {
                req.setAttribute("operation", "Edit");
                long id = Long.parseLong(idParam);
                image = Image.findById(id);
                req.setAttribute("image", image);
            } else {
                req.setAttribute("operation", "Add");
                image = new Image();
            }

            sess = HibernateUtil.currentSession();
            tx = sess.beginTransaction();
            
            image.setName(name);
            image.setTitle(title);
            image.setCaption(caption);
            image.setCreator(creator);
            image.setCopyright(copyright);
            //                image.setNotes(req.getParameter("notes"));

            if ( idParam == null ) {
                sess.save(image);
            }
            tx.commit();
            res.sendRedirect(req.getContextPath() + "/display/by-id/" +
                             image.getId());
            HibernateUtil.closeSession();
            return;
            //                req.setAttribute("errors", errors);
            //                req.getRequestDispatcher("/edit-add.jsp").include(req, res);
        } catch (NumberFormatException e) {
            errors.add("Invalid ID Specified.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/edit-add.jsp").forward(req, res);
        } catch (HibernateException e) {
            if ( tx != null ) {
                try {
                    tx.rollback();
                } catch (HibernateException e2) {
                    errors.add("Error rolling back transaction!");
                }
            }
            req.setAttribute("name", name);
            req.setAttribute("title", title);
            req.setAttribute("caption", caption);
            req.setAttribute("creator", creator);
            req.setAttribute("copyright", copyright);
            errors.add("Error retrieving Image: " + e.getMessage());
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/edit-add.jsp").forward(req, res);
        }
    }
}
