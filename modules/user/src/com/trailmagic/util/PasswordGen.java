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

import java.security.MessageDigest;
import com.trailmagic.util.tomcat.HexUtils;

public class PasswordGen {
    private static final String HASH_ALGORITHM = "MD5";
    public static final void main(String[] args) {
        try {
        String password = args[0];

        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        // this seems a little sketchy...are we handling charset right?
        // what's the result when someone uses a non-ascii char?
        String digest =
            HexUtils.convert(md.digest((new String(password)).getBytes()));
        System.out.println(digest);
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}