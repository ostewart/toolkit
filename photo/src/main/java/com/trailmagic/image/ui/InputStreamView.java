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

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.view.AbstractView;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.InputStream;

public class InputStreamView extends AbstractView {
    public static final String STREAM_KEY = "data";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String CONTENT_DISPOSITION_KEY = "contentDisposition";

    protected void renderMergedOutputModel(Map model,
                                           HttpServletRequest req,
                                           HttpServletResponse res)
        throws Exception {

        setupHeaders(model, res);
        writeOutput(model, res);
    }

    private void writeOutput(Map model, HttpServletResponse res) throws IOException {
        OutputStream out = res.getOutputStream();
        InputStream in = (InputStream)model.get(STREAM_KEY);

        IOUtils.copy(in, out);
    }

    private void setupHeaders(Map model, HttpServletResponse res) {
        setContentType((String)model.get(CONTENT_TYPE_KEY));
        // XXX: that doesn't seem to work, so do it myself
        res.setContentType((String)model.get(CONTENT_TYPE_KEY));
        if (model.containsKey(CONTENT_DISPOSITION_KEY)) {
            res.setHeader("Content-Disposition",
                          (String)model.get(CONTENT_DISPOSITION_KEY));
        }
    }
}
