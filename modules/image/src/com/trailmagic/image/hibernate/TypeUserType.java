package com.trailmagic.image.hibernate;

import com.trailmagic.image.ImageGroup;

public class TypeUserType extends EnumUserType {
    public TypeUserType() {
	super(ImageGroup.Type.class);
    }
}
