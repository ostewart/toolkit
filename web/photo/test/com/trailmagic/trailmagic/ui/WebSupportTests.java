package com.trailmagic.trailmagic.ui;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class WebSupportTests extends
        AbstractDependencyInjectionSpringContextTests {
    
    public void testGetDefaultMF() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String[] getConfigLocations() {
       return new String[]
           {"classpath:com/trailmagic/image/ui/applicationContext-test.xml"};
    }

}
