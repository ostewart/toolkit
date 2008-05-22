package com.trailmagic.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.savedrequest.SavedRequest;
import org.acegisecurity.util.PortResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebRequestTools {
    private PortResolver portResolver;
    private static Log log = LogFactory.getLog(WebRequestTools.class);
    
    public WebRequestTools(PortResolver portResolver) {
        super();
        this.portResolver = portResolver;
    }

    /**
     * Saves the passed request in the session to be retrieved at some later
     * point by {@link WebRequestTools#getSavedRequest(HttpSession)}.
     * @param request the request to save
     */
    public void saveCurrentRequest(HttpServletRequest request) {
        SavedRequest savedRequest =
            new SavedRequest(request, portResolver);
        if (log.isDebugEnabled()) {
            log.debug("SavedRequest added to Session: " + savedRequest);
        }

        // Store the HTTP request itself. Used by AbstractProcessingFilter
        // for redirection after successful authentication (SEC-29)
        request.getSession().setAttribute(AbstractProcessingFilter
                                          .ACEGI_SAVED_REQUEST_KEY, savedRequest);
    }

    /**
     * Retrieves a request previously saved by
     * {@link #saveCurrentRequest(HttpServletRequest)}.
     * @param session the HTTP session in which the request is stored
     * @return a saved request object, or <code>null</code> if none is stored
     */
    public SavedRequest getSavedRequest(HttpSession session) {
        return (SavedRequest) session.getAttribute(AbstractProcessingFilter
                                                   .ACEGI_SAVED_REQUEST_KEY);
    }

    /**
     * Returns a <code>String</code> containing the full request URL of the
     * specified request, including the query string, if any.
     * @param request an HTTP request object
     * @return a <code>String</code> containing the full request URL of the
     * specified request, including the query string, if any.
     */
    public String getFullRequestUrl(HttpServletRequest request) {
        StringBuffer sb = request.getRequestURL();
        if (request.getQueryString() != null) {
            sb.append("?");
            sb.append(request.getQueryString());
        }
        return sb.toString();
    }
}
