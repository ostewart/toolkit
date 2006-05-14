package com.trailmagic.image;

import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.AclObjectIdentityAware;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;

public class Camera implements AclObjectIdentityAware {
    private long m_id;
    private String m_name;
    private String m_manufacturer;
    private String m_format;

    public Camera(long id) {
        m_id = id;
    }

    public Camera() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getManufacturer() {
        return m_manufacturer;
    }

    public void setManufacturer(String mfr) {
        m_manufacturer = mfr;
    }

    public String getFormat() {
        return m_format;
    }

    public void setFormat(String format) {
        m_format = format;
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(Camera.class.getName(),
                                             Long.toString(getId()));
    }
}
