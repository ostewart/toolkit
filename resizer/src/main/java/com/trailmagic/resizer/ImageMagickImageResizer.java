package com.trailmagic.resizer;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImageMagickImageResizer implements ImageResizer {
    private CommandExcecutor executor;

    @Autowired
    public ImageMagickImageResizer(CommandExcecutor executor) {
        this.executor = executor;
    }

    @Override
    public File resizeImage(File srcFile, ImageFileInfo imageInfo, int shortestDimensionLength) throws ResizeFailedException {
        try {
            File destFile = File.createTempFile("image-resizer-output", "jpg");

            executor.exec(String.format("convert -quality 80 -resize '%s' '%s' '%s'",
                                        geometryString(imageInfo, shortestDimensionLength),
                                        srcFile.getAbsolutePath(),
                                        destFile.getAbsolutePath()));
            return destFile;
        } catch (IOException e) {
            throw new ResizeFailedException(e);
        }
    }

    private String geometryString(ImageFileInfo imageInfo, int shortestDimensionLength) {
        if (imageInfo.isLandscape()) {
            return "x" + shortestDimensionLength + ">";
        } else {
            return shortestDimensionLength + "x>";
        }
    }

    public File writeToTempFile(InputStream imageInputStream) throws IOException {
        File file = File.createTempFile("image-resizer-input", "jpg");
        IOUtils.copy(imageInputStream, new FileOutputStream(file));
        return file;
    }

    @Override
    public ImageFileInfo identify(File file) throws CouldNotIdentifyException {
        String output = executor.exec("identify " + file.getAbsolutePath());

        Pattern pattern = Pattern.compile(".*?([A-Z]+) (\\d+)x(\\d+).*?");

        Matcher matcher = pattern.matcher(output);

        if (!matcher.matches() || matcher.groupCount() != 3) {
            throw new CouldNotIdentifyException("Failed to match output: " + output);
        }
        String format = matcher.group(1);
        String height = matcher.group(2);
        String width = matcher.group(3);
        return new ImageFileInfo(Integer.parseInt(height), Integer.parseInt(width), mimeTypeFromFormat(format));
    }

    private String mimeTypeFromFormat(String format) throws CouldNotIdentifyException {
        if ("JPEG".equals(format.trim())) {
            return "image/jpeg";
        }
        throw new CouldNotIdentifyException("unknown format: " + format);
    }
}
