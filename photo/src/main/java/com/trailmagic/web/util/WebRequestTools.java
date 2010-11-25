package com.trailmagic.web.util;

import com.trailmagic.image.ImageGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;

@Service
public class WebRequestTools {
    private RequestCache requestCache = new HttpSessionRequestCache();
    private static Log log = LogFactory.getLog(WebRequestTools.class);

    public WebRequestTools() {
        super();
    }

    public void saveCurrentRequest(HttpServletRequest request) {
        requestCache.saveRequest(request, null);
    }

    public SavedRequest getSavedRequest(HttpServletRequest request, HttpServletResponse response) {
        return requestCache.getRequest(request, response);
    }

    /**
     * Returns a <code>String</code> containing the full request URL of the
     * specified request, including the query string, if any.
     *
     * @param request an HTTP request object
     * @return a <code>String</code> containing the full request URL of the
     *         specified request, including the query string, if any.
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
        } catch (IllegalArgumentException e) {
            throw new MalformedUrlException("invalid group type: " + groupTypeString, e);
        }

        if (!pathTokens.hasMoreTokens()) {
            return iri;
        }

        // process first (owner) arg
        iri.setScreenName(pathTokens.nextToken());

        // got user arg: show his/her groups
        if (!pathTokens.hasMoreTokens()) {
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
