package com.trailmagic.image.ui;

import com.trailmagic.image.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class EditAddServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        req.getRequestDispatcher("/edit-add.jsp").forward(req, res);
    }
}
