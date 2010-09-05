package com.trailmagic.resizer;

import java.io.File;
import java.util.List;

public interface ImageResizeService {
    public List<ImageFileInfo> scheduleResize(Long imageId, Long imageManifestationId);
}
