package com.trailmagic.image;

public class ImageFrame/* implements Comparable*/ {
    private ImageGroup m_imageGroup;
    //    private int m_position;
    private Image m_image;
    private String m_caption;

    public ImageFrame() {
    }

    public ImageGroup getImageGroup() {
        return m_imageGroup;
    }

    public void setImageGroup(ImageGroup group) {
        m_imageGroup = group;
    }
    /* get it from the List
    public int getPosition() {
        return m_position;
    }

    public void setPosition(int pos) {
        m_position = pos;
    }
    */
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
        /*
        return (obj instanceof ImageFrame) &&
            (this.getImageGroup().equals(((ImageFrame)obj).getImageGroup())) &&
            (this.getImage().equals(((ImageFrame)obj).getImage()));
        */
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
    }

    /*
    public int compareTo(Object obj) throws ClassCastException {
        ImageFrame other = (ImageFrame) obj;

        return (this.m_position - other.m_position);
    }

    public boolean equals(Object obj) {
        if ( !(obj instanceof ImageFrame) ) {
            return false;
        }

        return this.m_image.equals(((ImageFrame)obj).m_image) &&
            (this.m_position == ((ImageFrame)obj).m_position);
    }

    public int hashCode() {
        return m_position;
    }
    */
}
