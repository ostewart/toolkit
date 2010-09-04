package com.trailmagic.resizer;

public interface ImageResizeService {
    public void scheduleResize(Long imageId, Long imageManifestationId);
}
