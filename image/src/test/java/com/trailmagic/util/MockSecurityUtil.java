package com.trailmagic.util;

import com.trailmagic.user.User;

public class MockSecurityUtil extends SecurityUtil {
    private User currentUser;

    public MockSecurityUtil(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public User getCurrentUser() {
        return this.currentUser;
    }
}
