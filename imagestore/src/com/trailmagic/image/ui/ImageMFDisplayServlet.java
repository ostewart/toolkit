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

import java.sql.Blob;
import java.sql.SQLException;
import java.io.OutputStream;
import java.io.InputStream;


public class ImageMFDisplayServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        Collection errors = new ArrayList();
        try {
            String pathInfo = req.getPathInfo();
            if ( pathInfo == null ) {
                error(res);
                return;
            }
            StringTokenizer args = new StringTokenizer(pathInfo, "/");

            if ( !args.hasMoreTokens() ) {
                error(res);
                return;
            }
            String method = args.nextToken();
            if ( method.equals("by-id") ) {
                Session session = HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                
                Query query =
                    session.createQuery("select from com.trailmagic.image.HeavyImageManifestation as imf where imf.id = :id");
                query.setLong("id", Long.parseLong(args.nextToken()));
                
                HeavyImageManifestation imf =
                    (HeavyImageManifestation)query.uniqueResult();
                if ( imf != null ) {
                    res.setContentType(imf.getFormat());
                    Blob data = imf.getData();
                    InputStream img = data.getBinaryStream();

                    //                    java.io.FileInputStream img = new java.io.FileInputStream("/tmp/tiger.jpg");
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    OutputStream out = res.getOutputStream();
                    for ( len = img.read(bytes); len > 0;
                          len = img.read(bytes) ) {
                        out.write(bytes, 0, len);
                    }

                    if ( false ) { throw new SQLException("hi"); }

                    //                    OutputStream out = res.getOutputStream();
                    //                    out.write(imf.getData());
                    HibernateUtil.closeSession();
                    return;
                }
            }
        } catch (HibernateException e) {
            error(res);
        } catch (SQLException e) {
            error(res);
        }
        error(res);
    }

    private void error(HttpServletResponse res) {
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        //        HibernateUtil.closeSession();
    }
}
