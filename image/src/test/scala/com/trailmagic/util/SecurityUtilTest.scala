package com.trailmagic.util

import org.springframework.security.core.context.SecurityContextHolder
import org.junit.Assert.assertEquals
import org.springframework.security.authentication.TestingAuthenticationToken
import org.junit.{After, Test}
import org.springframework.security.core.Authentication
import com.trailmagic.user.security.ToolkitUserDetails
import com.trailmagic.user.User

class SecurityUtilTest {
  val securityUtil = new SecurityUtil

  @After
  def tearDown() {
    SecurityContextHolder.getContext.setAuthentication(null)
  }

  def setupAuthentication(auth: Authentication) {
    SecurityContextHolder.getContext.setAuthentication(auth)
  }

  @Test
  def testNullAuthenticationProducesNone {
    setupAuthentication(null)

    assertEquals(None, securityUtil.getCurrentUser)
  }

  @Test
  def testNullPrincipalProducesNone {
    setupAuthentication(new TestingAuthenticationToken(null, null))

    assertEquals(None, securityUtil.getCurrentUser)
  }

  @Test
  def testUserDetailsPrincipalProducesUser {
    val user = new User()
    setupAuthentication(new TestingAuthenticationToken(new ToolkitUserDetails(user), None))

    assertEquals(Some(user), securityUtil.getCurrentUser)
  }

  @Test(expected=classOf[ClassCastException])
  def testUnexpectedPrincipalTypeBlowsUp {
    setupAuthentication(new TestingAuthenticationToken("username", None))

    securityUtil.getCurrentUser
  }
}