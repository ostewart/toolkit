package com.trailmagic.image;

import java.util.Date;

public class Photo extends Image {
    private Lens m_lens;
    private Camera m_camera;
    private String m_notes;
    private Date m_captureDate;

    public Photo(long id) {
        super(id);
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

}
