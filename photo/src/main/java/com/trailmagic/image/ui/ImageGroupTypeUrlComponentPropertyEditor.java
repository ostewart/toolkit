package com.trailmagic.image.ui;

import com.trailmagic.image.ImageGroupType;

import java.beans.PropertyEditorSupport;


public class ImageGroupTypeUrlComponentPropertyEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        return getValue().toString();
    }

    @Override
    public void setAsText(String s) throws IllegalArgumentException {
        if (s.endsWith("s")) {
            s = s.substring(0, s.length() - 1);
        }
        setValue(ImageGroupType.fromString(s));
    }
}
