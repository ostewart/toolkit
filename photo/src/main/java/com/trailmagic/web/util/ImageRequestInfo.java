package com.trailmagic.web.util;

import com.trailmagic.image.ImageGroup;

public class ImageRequestInfo {
    private String screenName;
    private ImageGroup.Type imageGroupType;
    private String imageGroupName;
    private Long imageId;
    
    public String getScreenName() {
        return screenName;
    }
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
    public String getImageGroupName() {
        return imageGroupName;
    }
    public void setImageGroupName(String imageGroupName) {
        this.imageGroupName = imageGroupName;
    }
    public Long getImageId() {
        return imageId;
    }
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
    public ImageGroup.Type getImageGroupType() {
        return imageGroupType;
    }
    public void setImageGroupType(ImageGroup.Type imageGroupType) {
        this.imageGroupType = imageGroupType;
    }
}
