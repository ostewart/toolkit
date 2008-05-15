/*
 * Copyright (c) 2006 Oliver Stewart.  All Rights Reserved.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.trailmagic.user;

public interface UserFactory {
    public User createUser();
    
    /**
     * Retrieves the user identified by <code>screenName</code>.
     * @param screenName the screen name of the user
     * @return the user identified by <code>screenName</code>.
     * @throws NoSuchUserException if no user exists with the specified screen
     * name
     */
    public User getByScreenName(String screenName) throws NoSuchUserException;

    /**
     * Retrieves the user with the specified ID.
     * @param userId the ID of the user
     * @return the user with the specified ID
     * @throws NoSuchUserException if no user exists with the specified ID
     */
    public User getById(long userId) throws NoSuchUserException;
    
    public void save(User user);
}
