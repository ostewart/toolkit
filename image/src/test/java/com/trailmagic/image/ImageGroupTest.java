package com.trailmagic.image;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImageGroupTest {
    @Test
    public void testTypeFromString() {
        assertEquals(ImageGroupType.ROLL, ImageGroupType.fromString("roll"));
        assertEquals(ImageGroupType.ROLL, ImageGroupType.fromString("Roll"));
        assertEquals(ImageGroupType.ALBUM, ImageGroupType.fromString("Album"));
        assertEquals(ImageGroupType.ALBUM, ImageGroupType.fromString("album"));
    }
}
