package com.trailmagic.resizer;

import java.io.File;
import java.util.List;

public interface ImageResizeService {
    public List<File> scheduleResize(Long imageId, Long imageManifestationId);
}
