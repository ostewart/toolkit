package com.trailmagic.image;


public class Image {
    private long m_id;
    private String m_name;
    private String m_title;
    private String m_caption;
    private String m_copyright;
    private String m_creator;

    public Image(long id) {
        // nothing for now
        m_id = id;
    }

    public Image() {
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

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public String getCaption() {
        return m_caption;
    }

    public void setCaption(String caption) {
        m_caption = caption;
    }

    public String getCopyright() {
        return m_copyright;
    }

    public void setCopyright(String copyright) {
        m_copyright = copyright;
    }

    public String getCreator() {
        return m_creator;
    }

    public void setCreator(String creator) {
        m_creator = creator;
    }
}
