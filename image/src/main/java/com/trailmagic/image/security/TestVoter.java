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
package com.trailmagic.image.security;

import org.springframework.security.vote.AccessDecisionVoter;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.Authentication;
import org.apache.log4j.Logger;

public class TestVoter implements AccessDecisionVoter {
    private static Logger s_log = Logger.getLogger(TestVoter.class);

    public boolean supports(Class clazz) {
        s_log.debug("Supports called with: " + clazz);
        return true;
    }

    public boolean supports(ConfigAttribute attribute) {
        s_log.debug("Supports called with: " + attribute);
        return true;
    }

    public int vote(Authentication authentication, Object object,
                    ConfigAttributeDefinition config) {
        s_log.debug("vote called with: " + authentication + "; " + object
                    + "; " + config);
        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }
}