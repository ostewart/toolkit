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

public class DeleteServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        Collection errors = new ArrayList();
        String idParam = req.getParameter("id");
        Session sess;
        Transaction tx = null;
        if ( idParam != null ) {
            try {
                long id = Long.parseLong(idParam);
                Image image = Image.findById(id);
                sess = HibernateUtil.currentSession();
                tx = sess.beginTransaction();
                sess.delete(image);
                tx.commit();
                res.sendRedirect(req.getContextPath() + "/display");
                HibernateUtil.closeSession();
            } catch (NumberFormatException e) {
                errors.add("Invalid ID Specified.");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher("/delete-error.jsp").forward(req, res);
            } catch (HibernateException e) {
                if ( tx != null ) {
                    try {
                        tx.rollback();
                    } catch (HibernateException e2) {
                        errors.add("Error rolling back transaction!");
                    }
                } else {
                    errors.add(e.getMessage());
                }
                req.setAttribute("errors", errors);
                req.getRequestDispatcher("/delete-error.jsp").forward(req, res);

            }
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        doGet(req, res);
    }
}
