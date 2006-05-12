package com.trailmagic.image;

import com.trailmagic.user.Owned;
import com.trailmagic.user.User;
import java.util.SortedSet;
import java.util.Collection;

public class ImageGroup implements Owned {
    private long m_id;
    private String m_type;
    private String m_name;
    private String m_displayName;
    private String m_description;
    private SortedSet<ImageFrame> m_frames;
    private Collection<ImageGroup> m_subgroups;
    private ImageGroup m_supergroup;
    private User m_owner;

    public static final String ROLL_TYPE = "roll";
    public static final String ALBUM_TYPE = "album";

    public ImageGroup() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    /** URL worthy name **/
    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    /** name to be displayed to the user **/
    public String getDisplayName() {
        return m_displayName;
    }

    public void setDisplayName(String name) {
        m_displayName = name;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String desc) {
        m_description = desc;
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

    public SortedSet<ImageFrame> getFrames() {
        return m_frames;
    }

    public void setFrames(SortedSet<ImageFrame> frames) {
        m_frames = frames;
    }

    public int getNextFrameNumber() {
        ImageFrame lastFrame = m_frames.last();
        return lastFrame.getPosition() + 1;
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

    public Collection<ImageGroup> getSubgroups() {
        return m_subgroups;
    }

    public void setSubgroups(Collection<ImageGroup> subgroups) {
        m_subgroups = subgroups;
    }

    public User getOwner() {
        return m_owner;
    }

    public void setOwner(User owner) {
        m_owner = owner;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ImageGroup) &&
            (this.getName().equals(((ImageGroup)obj).getName())) &&
            (this.getOwner().equals(((ImageGroup)obj).getOwner())) &&
            (this.getType().equals(((ImageGroup)obj).getType()));
    }

}
