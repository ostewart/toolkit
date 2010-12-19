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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LogoutController {
    private RequestCache requestCache;

    @Autowired
    public LogoutController(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @RequestMapping("/logout")
    public void handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {
        SavedRequest savedRequest = requestCache.getRequest(req, res);

        HttpSession session = req.getSession(false);
        session.invalidate();

        Cookie terminate = new Cookie(TokenBasedRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, null);
        terminate.setMaxAge(0);
        res.addCookie(terminate);

        if (savedRequest != null) {
            res.sendRedirect(savedRequest.getRedirectUrl());
        } else {
            res.sendRedirect("/photo/albums/");
        }
    }
}