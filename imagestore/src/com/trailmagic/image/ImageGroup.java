package com.trailmagic.image;

import java.util.SortedSet;
import java.util.Collection;

public class ImageGroup {
    private long m_id;
    private String m_type;
    private String m_name;
    private SortedSet m_frames;
    private Collection m_subgroups;
    private ImageGroup m_supergroup;

    public ImageGroup() {
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

    public String getType() {
        return m_type;
    }

    public void setType(String type) {
        m_type = type;
    }

    public void addFrame(ImageFrame frame) {
        m_frames.add(frame);
    }

    public SortedSet getFrames() {
        return m_frames;
    }

    public void setFrames(SortedSet frames) {
        m_frames = frames;
    }

    public ImageGroup getSupergroup() {
        return m_supergroup;
    }

    public void setSupergroup(ImageGroup group) {
        m_supergroup = group;
    }

    public void addSubgroup(ImageGroup group) {
        group.setSupergroup(this);
        m_subgroups.add(group);
    }

    public Collection getSubgroups() {
        return m_subgroups;
    }

    public void setSubgroups(Collection subgroups) {
        m_subgroups = subgroups;
    }
}
