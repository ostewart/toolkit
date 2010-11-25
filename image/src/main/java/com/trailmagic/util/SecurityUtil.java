package com.trailmagic.util;

import com.trailmagic.user.User;
import com.trailmagic.user.security.ToolkitUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityUtil")
public class SecurityUtil {
    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            return null;
        }

        ToolkitUserDetails userDetails = (ToolkitUserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            return null;
        }

        return userDetails.getRealUser();
    }
}
