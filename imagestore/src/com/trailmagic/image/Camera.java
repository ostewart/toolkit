package com.trailmagic.image;

public class Camera {
    private long m_id;
    private String m_name;
    private String m_manufacturer;
    private String m_format;

    public Camera(long id) {
        m_id = id;
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


}
