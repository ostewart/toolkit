package com.trailmagic.image;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by: oliver on Date: Jun 26, 2010 Time: 10:51:01 PM
 */
public class ImageGroupTest {
    @Test
    public void testTypeFromString() {
        assertEquals(ImageGroup.Type.ROLL, ImageGroup.Type.fromString("roll"));
        assertEquals(ImageGroup.Type.ROLL, ImageGroup.Type.fromString("Roll"));
        assertEquals(ImageGroup.Type.ALBUM, ImageGroup.Type.fromString("Album"));
        assertEquals(ImageGroup.Type.ALBUM, ImageGroup.Type.fromString("album"));
    }
}
