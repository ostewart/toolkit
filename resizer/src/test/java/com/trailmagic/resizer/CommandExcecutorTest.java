package com.trailmagic.resizer;

import org.apache.commons.exec.launcher.CommandLauncher;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by: oliver on Date: Sep 3, 2010 Time: 9:27:01 PM
 */
public class CommandExcecutorTest {
    private CommandExcecutor executor;

    @Before
    public void setUp() {
        executor = new CommandExcecutor();
    }

    @Test
    public void testExec() throws Exception {
        List<String> output = executor.exec("echo foo");
        assertEquals("foo", output.get(0));
    }
}
