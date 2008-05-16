package com.trailmagic.image.util;

import java.io.File;
import java.io.InputStream;

public interface ImagesParser {
    public void parseFile(File metadataFile);
    public void parseFile(File metadataFile, File baseDir);
    public void parse(InputStream inputStream);
    public void parse(InputStream inputStream, File baseDir);
}