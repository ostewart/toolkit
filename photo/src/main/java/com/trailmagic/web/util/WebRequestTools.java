package com.trailmagic.web.util;

import com.trailmagic.image.ImageGroup;

import java.util.StringTokenizer;

import org.springframework.web.util.UrlPathHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.security.ui.savedrequest.SavedRequest;
import org.springframework.security.util.PortResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Service
public class WebRequestTools {
    private PortResolver portResolver;
    private static Log log = LogFactory.getLog(WebRequestTools.class);

    @Autowired
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
        request.getSession().setAttribute(AbstractProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY, savedRequest);
    }

    /**
     * Retrieves a request previously saved by
     * {@link #saveCurrentRequest(HttpServletRequest)}.
     * @param session the HTTP session in which the request is stored
     * @return a saved request object, or <code>null</code> if none is stored
     */
    public SavedRequest getSavedRequest(HttpSession session) {
        return (SavedRequest) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY);
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
    
    public ImageRequestInfo getImageRequestInfo(HttpServletRequest request)
        throws MalformedUrlException {
        ImageRequestInfo iri = new ImageRequestInfo();
        UrlPathHelper pathHelper = new UrlPathHelper();
        String myPath = pathHelper.getLookupPathForRequest(request);
        if (log.isDebugEnabled()) {
            log.debug("Lookup path: " +
                      pathHelper.getLookupPathForRequest(request));
        }
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");
        if (!pathTokens.hasMoreTokens()) {
            throw new MalformedUrlException("no group type");
        }
        String groupTypeString = pathTokens.nextToken();
        if (groupTypeString.length() < 1) {
            throw new MalformedUrlException("0 length group type");
        }

        // depluralize
        groupTypeString = groupTypeString.substring(0, groupTypeString.length() - 1);
        try {
            iri.setImageGroupType(ImageGroup.Type.fromString(groupTypeString));
        } catch(IllegalArgumentException e) {
            throw new MalformedUrlException("invalid group type: " + groupTypeString, e);
        }

        if ( !pathTokens.hasMoreTokens() ) {
            return iri;
        }

        // process first (owner) arg
        iri.setScreenName(pathTokens.nextToken());

        // got user arg: show his/her groups
        if ( !pathTokens.hasMoreTokens() ) {
            return iri;
        }

        // process second (group name) arg
        iri.setImageGroupName(pathTokens.nextToken());

        // got user and group args: show one group
        if (!pathTokens.hasMoreTokens()) {
            return iri;
        }

        // process third (frame number) arg
        try {
            iri.setImageId(Long.parseLong(pathTokens.nextToken().trim()));
        } catch (NumberFormatException e) {
            throw new MalformedUrlException("Invalid frame number.", e);
        }
        return iri;
    }
}
