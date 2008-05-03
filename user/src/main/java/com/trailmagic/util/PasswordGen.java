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
package com.trailmagic.util;

import com.trailmagic.user.UserLoginModule;

public class PasswordGen {
    public static final void main(String[] args) {
        String password = args[0];

        password =
            new String(UserLoginModule.encodePassword(password.toCharArray()));
        System.out.println(password);
    }
}