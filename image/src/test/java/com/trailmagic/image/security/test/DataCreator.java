package com.trailmagic.image.security.test;

import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupType;
import com.trailmagic.image.Photo;
import com.trailmagic.image.impl.ImageInitializer;
import com.trailmagic.user.NoSuchUserException;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.user.security.ToolkitUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DataCreator {
    private ImageInitializer imageInitializer;
    private UserRepository userRepository;
    private static final String TEST_USERNAME = "test";

    @Autowired
    public DataCreator(ImageInitializer imageInitializer, UserRepository userRepository) {
        this.imageInitializer = imageInitializer;
        this.userRepository = userRepository;
    }

    public Photo makePhoto(String name, boolean saved, User owner) {
        final Photo photo = new Photo();
        photo.setName(name);
        photo.setOwner(owner);
        photo.setDisplayName("test display");
        if (saved) {
            imageInitializer.saveNewImage(photo);
        }
        return photo;
    }

    public ImageGroup makeRoll(User owner, boolean saved) {
        ImageGroup group = new ImageGroup("testGroup", owner, ImageGroupType.ROLL);
        group.setDisplayName("test group");
        group.setUploadDate(new Date());
        if (saved) {
            imageInitializer.saveNewImageGroup(group);
        }
        return group;
    }


    public User createTestUser() {
        return createTestUser(TEST_USERNAME);
    }

    public User createTestUser(final String testUsername) {
        User testUser;
        try {
            testUser = userRepository.getByScreenName(testUsername);
        } catch (NoSuchUserException e) {
            testUser = new User(testUsername);
            testUser.setFirstName("Testy");
            testUser.setLastName("McTesterton");
            testUser.setPrimaryEmail("test@example.com");
            testUser.setPassword("password");
            userRepository.save(testUser);
        }
        return testUser;
    }

    public void authenticateUserWithAuthorities(User user, String... authorityNames) {
        List<GrantedAuthority> authorities = stringsToGrantedAuthorities(authorityNames);
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(new ToolkitUserDetails(user), "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void authenticateAnonymousUser() {
        final AnonymousAuthenticationToken token =
                new AnonymousAuthenticationToken("key", "anonymousUser", stringsToGrantedAuthorities("ROLE_ANONYMOUS", "ROLE_EVERYONE"));
        SecurityContextHolder.getContext().setAuthentication(token);
    }


    private List<GrantedAuthority> stringsToGrantedAuthorities(String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new GrantedAuthorityImpl(role));
        }
        return authorities;
    }
}
