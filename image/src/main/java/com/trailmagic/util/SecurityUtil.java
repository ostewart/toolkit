package com.trailmagic.util;

import com.trailmagic.user.User;
import com.trailmagic.user.security.ToolkitUserDetails;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityUtil")
public class SecurityUtil {
    public User getCurrentUser() {
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
