package com.trailmagic.image.client;

public class ImageClient implements ApplicationContextAware {
    private ApplicationContext m_appCtx;


    public ImageClient() {
    }

    public ApplicationContext getApplicationContext() {
        return m_appCtx;
    }
    
    public void setApplicationContext(ApplicationContext appCtx) {
        m_appCtx = appCtx;
    }

    public static final void main(String[] args) {
        
    }
}
