package com.trailmagic.image.ui;

import javax.servlet.http.HttpServletRequest;

import com.trailmagic.user.*;
import com.trailmagic.image.*;


public class LinkHelper {
    private HttpServletRequest m_request;

    public LinkHelper(HttpServletRequest req) {
        m_request = req;
    }

    /** 
     * /albums/user/album-name/image-id
     * this means that an image can't be in an album more than once
     * I think I can live with that
     **/
    public String getAlbumFrameUrl(ImageFrame frame) {
        return m_request.getContextPath() + m_request.getServletPath() +
            "/albums/" +
            frame.getImage().getOwner().getScreenName() + "/" +
            frame.getImageGroup().getName() + "/" + frame.getImage().getId();
    }

    public String getImageDisplayUrl(Image image) {
        return m_request.getContextPath() + "/display/by-id/" + image.getId();
    }

    public String getImageMFUrl(ImageManifestation imf) {
        return m_request.getContextPath() + "/mf/by-id/" + 
            imf.getId();
    }

    public String getImageMFUrl(Image image) {
        return m_request.getContextPath() + "/mf/by-id/" + 
            ((ImageManifestation)image.getManifestations().first()).getId();
    }

}
