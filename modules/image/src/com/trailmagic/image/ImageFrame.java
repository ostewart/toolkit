package com.trailmagic.image;

import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.AclObjectIdentityAware;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;

public class ImageFrame implements Comparable, AclObjectIdentityAware {
    private long m_id;
    private ImageGroup m_imageGroup;
    private int m_position;
    private Image m_image;
    private String m_caption;

    public ImageFrame() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }



    public ImageGroup getImageGroup() {
        return m_imageGroup;
    }

    public void setImageGroup(ImageGroup group) {
        m_imageGroup = group;
    }

    public int getPosition() {
        return m_position;
    }

    public void setPosition(int pos) {
        m_position = pos;
    }

    public Image getImage() {
        return m_image;
    }

    public void setImage(Image image) {
        m_image = image;
    }

    public String getCaption() {
        return m_caption;
    }

    public void setCaption(String caption) {
        m_caption = caption;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ImageFrame) &&
            (this.getImageGroup().equals(((ImageFrame)obj).getImageGroup())) &&
            (this.getImage().equals(((ImageFrame)obj).getImage()));
        /*
        if ( !(obj instanceof ImageFrame) ) {return false;}
        ImageGroup mine = getImageGroup();
        ImageGroup yours = ((ImageFrame)obj).getImageGroup();
        System.err.println("mine: " + mine + ", yours: " + yours);

        if (!(mine.equals(yours))) {
            return false;
        }
        if (!(this.getImage().equals(((ImageFrame)obj).getImage()))) {
            return false;
        }
        return false;
        */
    }


    public int compareTo(Object obj) throws ClassCastException {
        ImageFrame other = (ImageFrame) obj;
        // XXX: need to add something to this to make it consistent with equals
        return (this.m_position - other.m_position);
    }

    public int hashCode() {
        return m_position;
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(ImageFrame.class.getName(),
                                             Long.toString(getId()));
    }
}
