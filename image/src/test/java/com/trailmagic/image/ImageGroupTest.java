package com.trailmagic.image;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImageGroupTest {
    @Test
    public void testTypeFromString() {
        assertEquals(ImageGroup.Type.ROLL, ImageGroup.Type.fromString("roll"));
        assertEquals(ImageGroup.Type.ROLL, ImageGroup.Type.fromString("Roll"));
        assertEquals(ImageGroup.Type.ALBUM, ImageGroup.Type.fromString("Album"));
        assertEquals(ImageGroup.Type.ALBUM, ImageGroup.Type.fromString("album"));
    }
}
