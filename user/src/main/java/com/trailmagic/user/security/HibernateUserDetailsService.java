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
package com.trailmagic.user.security;

import com.trailmagic.user.Group;
import com.trailmagic.user.GroupFactory;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HibernateUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    private GroupFactory m_groupFactory;

    public void setUserFactory(UserRepository repository) {
        userRepository = repository;
    }

    public void setGroupFactory(GroupFactory groupFactory) {
        m_groupFactory = groupFactory;
    }

    /**
     * Locates the user based on the username. In the actual implementation,
     * the search may possibly be case insensitive, or case insensitive
     * depending on how the implementaion instance is configured. In this
     * case, the <code>UserDetails</code> object that comes back may have a
     * username that is of a different case than what was actually requested..
     *
     * @param username the username presented to the {@link
     *                 org.springframework.security.authentication.dao.DaoAuthenticationProvider}
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the
     *                                   user has no GrantedAuthority
     * @throws DataAccessException       if user could not be found for a
     *                                   repository-specific reason
     */
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, DataAccessException {

        User user = userRepository.getByScreenName(username);
        if (user == null) {
            throw new UsernameNotFoundException("No such user");
        }

        List<Group> groups = m_groupFactory.getForUser(user);
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Group group : groups) {
            authorities.add(new GrantedAuthorityImpl("ROLE_" + group.getName().toUpperCase()));
        }
        authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        authorities.add(new GrantedAuthorityImpl(user.getScreenName()));

        return new ToolkitUserDetails(user, authorities);
    }
}
