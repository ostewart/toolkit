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

  @Test(expected = classOf[IllegalStateException])
  def testNullAuthenticationThrowsException {
    setupAuthentication(null)

    securityUtil.getCurrentUser
  }

  @Test(expected = classOf[IllegalStateException])
  def testNullPrincipalThrowsException {
    setupAuthentication(new TestingAuthenticationToken(null, null))

    securityUtil.getCurrentUser
  }

  @Test
  def testUserDetailsPrincipalProducesUser {
    val user = new User("tester")
    setupAuthentication(new TestingAuthenticationToken(new ToolkitUserDetails(user), None))

    assertEquals(user, securityUtil.getCurrentUser)
  }

  @Test(expected=classOf[ClassCastException])
  def testUnexpectedPrincipalTypeBlowsUp {
    setupAuthentication(new TestingAuthenticationToken("username", None))

    securityUtil.getCurrentUser
  }
}