package com.trailmagic.image;

import java.util.Collection;

public class ImageCD {
    private long m_id;
    private int m_number;
    private String m_description;
    private Collection m_images;

    public ImageCD() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public int getNumber() {
        return m_number;
    }

    public void setNumber(int number) {
        m_number = number;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String desc) {
        m_description = desc;
    }

    public Collection getImages() {
        return m_images;
    }

    public void setImages(Collection images) {
        m_images = images;
    }
}
