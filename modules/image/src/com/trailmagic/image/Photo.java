package com.trailmagic.image;

import java.util.Date;

public class Photo extends Image {
    private Lens m_lens;
    private Camera m_camera;
    private String m_notes;
    private Date m_captureDate;
    private ImageGroup m_roll;

    public Photo(long id) {
        super(id);
    }

    public Photo() {
    }

    public Photo(Image image) {
        super(image);
    }

    public Photo(Photo photo) {
        super(photo);

        setLens(photo.getLens());
        setCamera(photo.getCamera());
        setNotes(photo.getNotes());
        setCaptureDate(photo.getCaptureDate());
        setRoll(photo.getRoll());
    }

    public Lens getLens() {
        return m_lens;
    }

    public void setLens(Lens lens) {
        m_lens = lens;
    }

    public Camera getCamera() {
        return m_camera;
    }

    public void setCamera(Camera camera) {
        m_camera = camera;
    }

    public String getNotes() {
        return m_notes;
    }

    public void setNotes(String notes) {
        m_notes = notes;
    }

    public Date getCaptureDate() {
        return m_captureDate;
    }

    public void setCaptureDate(Date date) {
        m_captureDate = date;
    }

    public ImageGroup getRoll() {
        return m_roll;
    }

    // XXX: assert(m_imageGroup.getType().equals("roll"));
    public void setRoll(ImageGroup group) {
        m_roll = group;
    }
}
