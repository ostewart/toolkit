package com.trailmagic.image.security;

import com.trailmagic.image.ImageService;
import com.trailmagic.image.Photo;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.user.security.ToolkitUserDetails;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-global.xml",
                                   "classpath:applicationContext-user.xml",
                                   "classpath:applicationContext-imagestore.xml",
                                   "classpath:applicationContext-imagestore-authorization.xml"})
@Transactional
public class SecurityIntegrationTest {
    @Autowired
    private ImageSecurityService service;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserRepository userRepository;
    private User testUser;

    @Test
    public void testMakePrivate() {
        setupAuthenticatedUser();
        final Photo photo = testPhoto();

        assertFalse("After creation, a photo should not be public", service.isPublic(photo));

        service.makePublic(photo);
        assertTrue("After makePublic call, the photo should be public", service.isPublic(photo));

        service.makePrivate(photo);
        assertFalse("After makePrivate call, the photo should not be public", service.isPublic(photo));
    }

    @Test
    public void testAddPermission() {
        setupAuthenticatedUser();
        final Photo photo = testPhoto();

        assertFalse("After creation, a photo should not be public", service.isPublic(photo));
        service.addReadPermission(photo, testUser);
        assertTrue("After addReadPermission call, should be readable", service.isReadableByUser(photo, testUser));
    }


    private Photo testPhoto() {
        final Photo photo = new Photo();
        photo.setId(1L);
        photo.setName("test");
        photo.setOwner(testUser);
        photo.setDisplayName("test display");
        imageService.saveNewImage(photo);
        return photo;
    }

    private User setupAuthenticatedUser() {
        testUser = new User("test");
        testUser.setFirstName("Testy");
        testUser.setLastName("McTesterton");
        testUser.setPrimaryEmail("test@example.com");
        testUser.setPassword("password");
        userRepository.save(testUser);

        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(new ToolkitUserDetails(testUser), "password",
                                                        new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_EVERYONE")});
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return testUser;
    }
}
