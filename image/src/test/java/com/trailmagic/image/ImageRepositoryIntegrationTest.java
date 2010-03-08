package com.trailmagic.image;

import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-global.xml",
                                   "classpath:com/trailmagic/image/applicationContext-test.xml",
                                   "classpath:applicationContext-user.xml",
                                   "classpath:applicationContext-imagestore.xml",
                                   "classpath:applicationContext-imagestore-authorization.xml"})
@Transactional
public class ImageRepositoryIntegrationTest {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired HibernateTemplate hibernateTemplate;
    private User testUser;
    private static final String TEST_PHOTO_NAME = "testPhoto";

    public ImageRepository getImageRepository() {
        return imageRepository;
    }

    public void setUserFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Before
    public void onSetUpInTransaction() {
        testUser = userRepository.createUser();
        testUser.setScreenName("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password");
        testUser.setPrimaryEmail("test@example.com");
        userRepository.save(testUser);
    }

    @Test
    public void testNewInstance() {
        setupAuthenticatedUser();

        try {
            jdbcTemplate.queryForObject("select name from images where name = ?", new Object[]{TEST_PHOTO_NAME}, String.class);
            fail();
        } catch (EmptyResultDataAccessException e) {
            // should have none
        }


        Photo newPhoto = new Photo();
        assertNotNull("Should create a new Photo", newPhoto);
        newPhoto.setName(TEST_PHOTO_NAME);
        newPhoto.setDisplayName("Test photo");
        newPhoto.setOwner(testUser);

        imageRepository.saveNew(newPhoto);

        hibernateTemplate.flush();
        assertTrue(newPhoto.getId() != 0);
        assertNotNull(jdbcTemplate.queryForObject("select name from images where name = ?",
                                                  new Object[]{TEST_PHOTO_NAME},
                                                  String.class));
    }

    private void setupAuthenticatedUser() {
        final SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(testUser, "password",
                                                                          new GrantedAuthority[]{new GrantedAuthorityImpl(testUser.getScreenName())}));
    }


}
