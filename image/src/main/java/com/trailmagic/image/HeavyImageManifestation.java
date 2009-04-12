/*
 * Copyright (c) 2004, 2005 Oliver Stewart.  All Rights Reserved.
 */
package com.trailmagic.image;

import java.sql.Blob;
import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.AclObjectIdentityAware;
import org.springframework.security.acl.basic.NamedEntityObjectIdentity;

public class HeavyImageManifestation extends ImageManifestation
    implements AclObjectIdentityAware {

    //    private byte[] m_data;
    private Blob m_data;

    public HeavyImageManifestation() {
        super();
    }

    public Blob getData() {
        return m_data;
    }

    public void setData(Blob data) {
        m_data = data;
    }
    /*
    public byte[] getData() {
        return m_data;
    }

    public void setData(byte[] data) {
        m_data = data;
    }
    */

    public AclObjectIdentity getAclObjectIdentity() {
        // pretend we're an ImageManifestation...really the same thing
        // in terms of identity
        return new NamedEntityObjectIdentity(ImageManifestation.class
                                             .getName(),
                                             Long.toString(getId()));
    }
}
