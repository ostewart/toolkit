package com.trailmagic.image;

import com.trailmagic.test.AbstractHibernateTests;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

public class ImageRepositoryIntegrationTest extends AbstractHibernateTests {
    private ImageRepository imageRepository;
    private UserRepository userRepository;
    private User testUser;
    private static final String TEST_PHOTO_NAME = "testPhoto";

    public ImageRepositoryIntegrationTest() {
        super();
        setAutowireMode(AUTOWIRE_BY_NAME);
    }

    public ImageRepository getImageRepository() {
        return imageRepository;
    }

    public void setUserFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    protected void onSetUpInTransaction() {
        testUser = userRepository.createUser();
        testUser.setScreenName("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password");
        testUser.setPrimaryEmail("test@example.com");
        userRepository.save(testUser);
    }

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

        getHibernateTemplate().flush();
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
