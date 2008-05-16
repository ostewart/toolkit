package com.trailmagic.image;

import com.trailmagic.test.AbstractHibernateTests;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;

public class ImageFactoryTests extends
        AbstractHibernateTests {
    private ImageRepository imageFactory;
    private UserFactory userFactory;
    private User testUser;
    private static final String TEST_PHOTO_NAME = "testPhoto";

    public ImageFactoryTests() {
        super();
        setAutowireMode(AUTOWIRE_BY_NAME);
    }

    public ImageRepository getImageFactory() {
        return imageFactory;
    }

    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    public void setImageFactory(ImageRepository imageFactory) {
        this.imageFactory = imageFactory;
    }
    
    protected void onSetUpInTransaction() {
        testUser = userFactory.createUser();
        testUser.setScreenName("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password");
        testUser.setPrimaryEmail("test@example.com");
        userFactory.save(testUser);
    }

    public void testNewInstance() {
        Photo newPhoto = new Photo();
        assertNotNull("Should create a new Photo", newPhoto);
        newPhoto.setName(TEST_PHOTO_NAME);
        newPhoto.setDisplayName("Test photo");
        newPhoto.setOwner(testUser);
        imageFactory.save(newPhoto);
        getHibernateTemplate().flush();
        assertTrue(newPhoto.getId() != 0);
        assertNotNull(jdbcTemplate.queryForObject("select name from images where name = ?",
                                                  new Object[] {TEST_PHOTO_NAME},
                                                  String.class));
    }


}
