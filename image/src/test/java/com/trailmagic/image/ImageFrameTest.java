package com.trailmagic.image;

import com.trailmagic.image.security.SecurityTestHelper;
import com.trailmagic.user.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by: oliver on Date: Sep 8, 2010 Time: 5:57:20 PM
 */
public class ImageFrameTest {
    @Before
    public void setUp() {
        new SecurityTestHelper().disableSecurityInterceptor();
    }

    @Test
    public void testPreviousFrame() {
        ImageGroup group = newImageGroup();
        addFrames(group, 1, 2, 3);
        ImageFrame startFrame = index(1, group.getFrames());

        assertEquals(1, startFrame.previous().getPosition());
        assertEquals(2, index(2, group.getFrames()).previous().getPosition());
    }

    @Test
    public void testPreviousFrameAtHeadIsNull() {
        ImageGroup group = newImageGroup();
        addFrames(group, 1, 2, 3);
        ImageFrame startFrame = index(0, group.getFrames());

        assertNull(startFrame.previous());
    }

    @Test
    public void testNextFrame() {
        ImageGroup group = newImageGroup();
        addFrames(group, 1, 2, 3);
        ImageFrame startFrame = index(1, group.getFrames());

        assertEquals(3, startFrame.next().getPosition());
        assertEquals(2, index(0, group.getFrames()).next().getPosition());
    }

    @Test
    public void testNextFrameAtTailIsNull() {
        ImageGroup group = newImageGroup();
        addFrames(group, 1, 2, 3);
        ImageFrame startFrame = index(2, group.getFrames());

        assertNull(startFrame.next());
    }

    private ImageGroup newImageGroup() {
        return new ImageGroup("test", new User("tester"), ImageGroupType.ROLL);
    }

    private ImageFrame index(int index, SortedSet<ImageFrame> frames) {
        Iterator<ImageFrame> frameIterator = frames.iterator();
        for (int i = 0; i <= index; i++) {
            ImageFrame frame = frameIterator.next();
            if (i == index) {
                return frame;
            }
        }
        throw new IllegalStateException("Frame not found at index " + index);
    }

    private void addFrames(ImageGroup group, int... positions) {
        for (int position : positions) {
            group.addFrame(frame(position));
        }
    }

    private ImageFrame frame(int pos) {
        ImageFrame imageFrame = new ImageFrame(new Image());
        imageFrame.setPosition(pos);
        return imageFrame;
    }
}
