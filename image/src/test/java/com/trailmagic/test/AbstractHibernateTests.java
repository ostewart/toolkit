package com.trailmagic.test;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public abstract class AbstractHibernateTests extends
        AbstractTransactionalDataSourceSpringContextTests {
    
    private HibernateTemplate hibernateTemplate;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {"applicationContext-global.xml",
                "applicationContext-imagestore.xml",
                "applicationContext-imagestore-authorization.xml",
                "applicationContext-user.xml",
                "com/trailmagic/image/applicationContext-test.xml"};
    }
}
