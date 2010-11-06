package com.trailmagic.image.impl;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.Image;
import com.trailmagic.image.ImageManifestationService;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.resizer.ImageFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class ImageManifestationServiceImpl implements ImageManifestationService {
    private HibernateUtil hibernateUtil;
    private ImageInitializer imageInitializer;
    private ImageRepository imageRepository;

    @Autowired
    public ImageManifestationServiceImpl(HibernateUtil hibernateUtil, ImageInitializer imageInitializer, ImageRepository imageRepository) {
        this.hibernateUtil = hibernateUtil;
        this.imageInitializer = imageInitializer;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public void createManifestation(Image image, ImageFileInfo info, boolean original) throws IOException {
        image = imageRepository.getById(image.getId());
        final HeavyImageManifestation manifestation = new HeavyImageManifestation();
        manifestation.setData(hibernateUtil.toBlob(info.getFile()));
        manifestation.setOriginal(original);
        manifestation.setFormat(info.getFormat());
        manifestation.setHeight(info.getHeight());
        manifestation.setWidth(info.getWidth());
        image.addManifestation(manifestation);
        imageInitializer.saveNewImageManifestation(manifestation, original);
    }
}
