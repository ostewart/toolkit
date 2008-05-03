/*
 * Copyright (c) 2006 Oliver Stewart.  All Rights Reserved.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.trailmagic.image.ui;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;
import org.acegisecurity.ui.savedrequest.SavedRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class LogoutController extends AbstractController {
    private static final String LOGOUT_VIEW = "logout";

    public ModelAndView handleRequestInternal(HttpServletRequest req,
                                              HttpServletResponse res)
        throws Exception {

        HttpSession session = req.getSession(false);
        SavedRequest savedRequest =
            (SavedRequest) session.getAttribute(AbstractProcessingFilter
                                                .ACEGI_SAVED_REQUEST_KEY);
        if (session != null) {
            session.invalidate();
        }
        Cookie terminate =
            new Cookie(TokenBasedRememberMeServices
                       .ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE_KEY,
                       null);
        terminate.setMaxAge(0);
        res.addCookie(terminate);


        if (savedRequest != null) {
            res.sendRedirect(savedRequest.getFullRequestUrl());
        } else {
            res.sendRedirect("/photo/albums/");
        }
        return null;
    }
}