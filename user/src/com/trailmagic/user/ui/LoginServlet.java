package com.trailmagic.user.ui;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;


public class LoginServlet extends HttpServlet {
    private static final String JSP_PATH = "/WEB-INF/jsp/login.jsp";
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        req.getRequestDispatcher(JSP_PATH).include(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        req.getRequestDispatcher(JSP_PATH).include(req, res);
    }
}
