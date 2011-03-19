package com.trailmagic.util

import com.trailmagic.user.User
import com.trailmagic.user.security.ToolkitUserDetails
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service("securityUtil")
class SecurityUtil {
  def getCurrentUser: Option[User] = {
    val authentication: Authentication = SecurityContextHolder.getContext.getAuthentication

    authentication match {
      case null => None
      case _ => authentication.getPrincipal.asInstanceOf[ToolkitUserDetails] match {
        case null => None
        case userDetails => Some(userDetails.getRealUser)
      }
    }
  }
}