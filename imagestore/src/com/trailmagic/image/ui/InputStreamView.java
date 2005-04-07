package com.trailmagic.image.ui;

import org.springframework.web.servlet.view.AbstractView;
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
        
        setContentType((String)model.get(CONTENT_TYPE_KEY));
        // XXX: that doesn't seem to work, so do it myself
        res.setContentType((String)model.get(CONTENT_TYPE_KEY));
        if (model.containsKey(CONTENT_DISPOSITION_KEY)) {
            res.setHeader("Content-Disposition",
                          (String)model.get(CONTENT_DISPOSITION_KEY));
        }
        OutputStream out = res.getOutputStream();
        InputStream in = (InputStream)model.get(STREAM_KEY);
        byte[] buf = new byte[1024];
        int numRead = 0;

        // move along now
        while ( in.available() > 0 ) {
            numRead = in.read(buf);
            out.write(buf, 0, numRead);
        }
    }
}
