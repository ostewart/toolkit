package com.trailmagic.image;

public class Lens {
    private long m_id;
    private String m_name;
    private String m_manufacturer;
    private int m_focalLength;
    private int m_minAperature;
    private int m_maxAperature;

    public Lens(long id) {
        m_id = id;
    }

    public Lens() {
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

    public int getFocalLength() {
        return m_focalLength;
    }

    public void setFocalLength(int length) {
        m_focalLength = length;
    }

    public int getMinAperature() {
        return m_minAperature;
    }

    public void setMinAperature(int aperature) {
        m_minAperature = aperature;
    }

    public int getMaxAperature() {
        return m_maxAperature;
    }

    public void setMaxAperature(int aperature) {
        m_maxAperature = aperature;
    }

}
