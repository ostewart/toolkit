package com.trailmagic.user;

public interface UserFactory {
    public User newInstance();
    public User getByScreenName(String screenName);
    public User getById(long id);
}
