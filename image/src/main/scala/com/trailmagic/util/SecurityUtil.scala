package com.trailmagic.util

import com.trailmagic.user.User
import com.trailmagic.user.security.ToolkitUserDetails
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service("securityUtil")
class SecurityUtil {
  def getCurrentUser: User = {
    val authentication: Authentication = SecurityContextHolder.getContext.getAuthentication

    authentication match {
      case null => throw new IllegalStateException("Must have a signed-in user–no Authentication present")
      case _ => authentication.getPrincipal.asInstanceOf[ToolkitUserDetails] match {
        case null => throw new IllegalStateException("Must have a signed-in user–no principal present")
        case userDetails => userDetails.getRealUser
      }
    }
  }
}