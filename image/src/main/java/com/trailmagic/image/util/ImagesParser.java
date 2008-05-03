package com.trailmagic.image.util;

import java.io.File;
import java.io.InputStream;

public interface ImagesParser {
    public void parseFile(File metadataFile);
    public void parseFile(File baseDir, File metadataFile);
    public void parse(InputStream inputStream);
}