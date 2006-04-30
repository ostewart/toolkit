/*
 * Copyright (c) 2004, 2005 Oliver Stewart.  All Rights Reserved.
 */
package com.trailmagic.image;

import java.sql.Blob;

public class HeavyImageManifestation extends ImageManifestation {
    //    private byte[] m_data;
    private Blob m_data;

    public HeavyImageManifestation() {
        super();
    }

    public Blob getData() {
        return m_data;
    }

    public void setData(Blob data) {
        m_data = data;
    }
    /*
    public byte[] getData() {
        return m_data;
    }

    public void setData(byte[] data) {
        m_data = data;
    }
    */

}
