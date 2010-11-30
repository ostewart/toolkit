package com.trailmagic.image.security;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.impl.ImageInitializer;
import com.trailmagic.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-global.xml",
                                   "classpath:com/trailmagic/image/applicationContext-test.xml",
                                   "classpath:applicationContext-user.xml",
                                   "classpath:applicationContext-imagestore.xml",
                                   "classpath:applicationContext-imagestore-authorization.xml"})
@Transactional
public class ImageSecurityServiceAccessIntegrationTest {
    @Autowired
    private ImageSecurityService securityService;
    @Autowired
    private ImageInitializer imageInitializer;

    @Test(expected = AccessDeniedException.class)
    public void testAddOwnerAclToImageFailsWithoutUser() throws IOException {
        setupNoAuthenticatedUser();
        securityService.addOwnerAcl(new Image());
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddOwnerAclToImageGroupFailsWithoutUser() throws IOException {
        setupNoAuthenticatedUser();
        securityService.addOwnerAcl(new ImageGroup("name", new User("owner"), ImageGroup.Type.ROLL));
    }

    @Test(expected = AccessDeniedException.class)
    public void testMakePublicFailsWithoutUser() throws IOException {
        setupNoAuthenticatedUser();
        securityService.makePublic(new Image());
    }

    private void setupNoAuthenticatedUser() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser", Arrays.<GrantedAuthority>asList(new GrantedAuthorityImpl("ROLE_ANONYMOUS"))));

    }
}
