package com.trailmagic.image.ui;

import com.trailmagic.image.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Transaction;



public class GroupServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();

        try {
            // process / request: list groups
            if (pathInfo == null || pathInfo.equals("")
                || pathInfo.equals("/")) {

                Session sess = HibernateUtil.currentSession();
                
                Query query =
                    sess.createQuery("from com.trailmagic.image.ImageGroup");
                Collection groups = query.list();
                
                req.setAttribute("groups", groups);
                req.getRequestDispatcher("/groups.jsp").include(req, res);
                HibernateUtil.closeSession();
            } else if (pathInfo.startsWith("/new")) {
                req.getRequestDispatcher("/new-group.jsp").include(req, res);
            }
        } catch (HibernateException e) {
            Collection errors = new ArrayList();
            errors.add("Error getting groups: ");
            errors.add(e.getMessage());
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/groups.jsp").include(req, res);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        /*
        try {        
        } catch (HibernateException e) {
            if ( tx != null ) {
                try {
                    tx.rollback();
                } catch (HibernateException e2) {
                    errors.add("Error rolling back transaction!");
                }
            }
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/edit-add.jsp").forward(req, res);
        }
        */
    }
}
