package com.trailmagic.image;

import java.sql.Blob;
import java.util.SortedSet;

public class ImageManifestation implements Comparable {
    private long m_id;
    private Image m_image;
    private int m_height;
    private int m_width;
    private String m_format;
    private boolean m_original;
    private String m_name;
    //    private Blob m_data;
    private byte[] m_data;
    
    public ImageManifestation() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public Image getImage() {
        return m_image;
    }

    public void setImage(Image image) {
        m_image = image;
        //        SortedSet mfs = image.getManifestations();
        //        mfs.add(this);
    }

    public int getHeight() {
        return m_height;
    }
    
    public void setHeight(int height) {
        m_height = height;
    }

    public int getWidth() {
        return m_width;
    }

    public void setWidth(int width) {
        m_width = width;
    }

    public String getFormat() {
        return m_format;
    }

    public void setFormat(String format) {
        m_format = format;
    }

    public boolean isOriginal() {
        return m_original;
    }

    public void setOriginal(boolean original) {
        m_original = original;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    /*
    public Blob getData() {
        return m_data;
    }

    public void setData(Blob data) {
        m_data = data;
    }
    */
    public byte[] getData() {
        return m_data;
    }

    public void setData(byte[] data) {
        m_data = data;
    }

    private int getArea() {
        return m_height * m_width;
    }

    public int compareTo(Object obj) throws ClassCastException {
        ImageManifestation other = (ImageManifestation) obj;

        int difference = this.getArea() - other.getArea();
        if (difference == 0) {
            return (int)(this.getId() - other.getId());
        } else {
            return difference;
        }
    }

    public boolean equals(Object obj) {
        if ( !(obj instanceof ImageManifestation) ) {
            return false;
        }

        return this.getId() == ((ImageManifestation)obj).getId();
    }

    public int hashCode() {
        return new Long(getId()).hashCode();
    }
}