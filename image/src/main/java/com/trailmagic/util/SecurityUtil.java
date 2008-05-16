package com.trailmagic.util;

import com.trailmagic.user.User;
import com.trailmagic.user.security.ToolkitUserDetails;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

public class SecurityUtil {
    public static User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        ToolkitUserDetails userDetails =
            (ToolkitUserDetails) securityContext.getAuthentication().getPrincipal();
        if (userDetails != null) {
            return userDetails.getRealUser();
        } else {
            return null;
        }
    }
}
