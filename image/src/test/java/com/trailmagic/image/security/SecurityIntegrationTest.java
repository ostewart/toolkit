package com.trailmagic.image.security;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageService;
import com.trailmagic.image.Photo;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.user.security.ToolkitUserDetails;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hibernate.Hibernate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-global.xml",
                                   "classpath:com/trailmagic/image/applicationContext-test.xml",
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
    public void testMakePhotoPrivate() {
        setupAuthenticatedUser();
        final Photo photo = testPhoto();

        assertFalse("After creation, a photo should not be public", service.isPublic(photo));

        service.makePublic(photo);
        assertTrue("After makePublic call, the photo should be public", service.isPublic(photo));

        service.makePrivate(photo);
        assertFalse("After makePrivate call, the photo should not be public", service.isPublic(photo));
        assertFalse("After makePrivate call, should not be readable", service.isReadableByRole(photo, "ROLE_EVERYONE"));
        assertFalse("After makePrivate call, isAvailable should also work", service.isAvailableToRole(photo, "ROLE_EVERYONE", BasePermission.READ));

    }

    @Test
    public void testMakeImageManifestationPrivate() {
        setupAuthenticatedUser();
        final Photo photo = testPhoto();
        HeavyImageManifestation mf = manifestationForPhoto(photo);

        assertFalse("After creation, a manifestation should not be public", service.isPublic(mf));

        service.makePublic(photo);
        assertTrue("After makePublic call on its Image, the manifestation should be public", service.isPublic(mf));

        service.makePrivate(photo);
        assertFalse("After makePrivate call, the manifestation should not be public", service.isPublic(mf));
        assertFalse("After makePrivate call, should not be readable", service.isReadableByRole(mf, "ROLE_EVERYONE"));
        assertFalse("After makePrivate call, isAvailable should also work", service.isAvailableToRole(mf, "ROLE_EVERYONE", BasePermission.READ));
    }

    @Test
    public void testMakeImageFramePrivate() {
        setupAuthenticatedUser();
        final Photo photo = testPhoto();

        ImageGroup group = new ImageGroup("testGroup", testUser, ImageGroup.Type.ROLL);

        ImageFrame frame = new ImageFrame();
        frame.setId(3L);
        frame.setImage(photo);
        frame.setImageGroup(group);

        group.addFrame(frame);

        assertFalse("After creation, a frame should not be public", service.isPublic(frame));

        service.makePublic(photo);
        assertTrue("After makePublic call on its Image, the frame should be public", service.isPublic(frame));
        assertTrue("After makePublic call, should be readable", service.isReadableByRole(frame, "ROLE_EVERYONE"));

        service.makePrivate(photo);
        assertFalse("After makePrivate call, the frame should not be public", service.isPublic(frame));
        assertFalse("After makePrivate call, should not be readable", service.isReadableByRole(frame, "ROLE_EVERYONE"));
        assertFalse("After makePrivate call, isAvailable should also work", service.isAvailableToRole(frame, "ROLE_EVERYONE", BasePermission.READ));
    }

    @Test
    public void testMakeImageGroupPrivate() {
        setupAuthenticatedUser();
        final Photo photo = testPhoto();

        ImageGroup group = new ImageGroup("testGroup", testUser, ImageGroup.Type.ROLL);
        group.setDisplayName("test group");
        group.setName("testGroup");
        group.setUploadDate(new Date());

        ImageFrame frame = new ImageFrame();
        frame.setId(3L);
        frame.setImage(photo);
        frame.setImageGroup(group);

        group.addFrame(frame);

        imageService.saveNewImageFrame(frame);
        imageService.saveNewImageGroup(group);

        assertFalse("After creation, a group should not be public", service.isPublic(group));

        service.makePublic(group);
        assertTrue("After makePublic call on its ImageGroup, the ImageGroup should be public", service.isPublic(group));
        assertTrue("After makePublic call, should be readable", service.isReadableByRole(group, "ROLE_EVERYONE"));

        service.makePrivate(group);
        assertFalse("After makePrivate call, should not be public", service.isPublic(group));
        assertFalse("After makePrivate call, should not be readable", service.isReadableByRole(group, "ROLE_EVERYONE"));
        assertFalse("After makePrivate call, isAvailable should also work", service.isAvailableToRole(group, "ROLE_EVERYONE", BasePermission.READ));
    }

    private HeavyImageManifestation manifestationForPhoto(Photo photo) {
        HeavyImageManifestation mf = new HeavyImageManifestation();
        mf.setId(2L);
        mf.setFormat("image/jpeg");
        mf.setHeight(0);
        mf.setWidth(0);
        mf.setOriginal(true);
        mf.setData(Hibernate.createBlob(new byte[0]));
        photo.addManifestation(mf);

        return mf;
    }

    @Test
    public void testAddPermission() {
        setupAuthenticatedUser();
        final Photo photo = testPhoto();

        assertFalse("After creation, a photo should not be public", service.isPublic(photo));
        service.addReadPermission(photo, testUser);
        assertTrue("After addReadPermission call, should be readable", service.isReadableByUser(photo, testUser));
        assertTrue("After addReadPermission call, isAvailable should also work", service.isAvailableToUser(photo, testUser, BasePermission.READ));
    }

    @Test
    public void testAddPermissionAppliesToManifestation() {
        setupAuthenticatedUser();
        final Photo photo = testPhoto();
        HeavyImageManifestation mf = manifestationForPhoto(photo);

        assertFalse("After creation, a photo should not be public", service.isPublic(mf));
        service.addReadPermission(photo, testUser);
        assertTrue("After addReadPermission call, should be readable", service.isReadableByUser(mf, testUser));
        assertTrue("After addReadPermission call, isAvailable should also work", service.isAvailableToUser(mf, testUser, BasePermission.READ));
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
