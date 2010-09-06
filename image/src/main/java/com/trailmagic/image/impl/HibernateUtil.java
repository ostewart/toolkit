package com.trailmagic.image.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;

@Service
public class HibernateUtil {
    public Blob toBlob(File srcFile) throws IOException {
        return Hibernate.createBlob(new FileInputStream(srcFile));
    }

}
