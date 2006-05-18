package com.trailmagic.image;

import com.trailmagic.user.User;
import java.util.List;

public interface ImageGroupFactory {
    public int ROLL_TYPE = 0;
    public int ALBUM_TYPE = 1;

    public ImageGroup newInstance(int type);
    public ImageGroup getAlbumByOwnerAndName(User owner, String albumName);
    public ImageGroup getRollByOwnerAndName(User owner, String rollName);
    public ImageFrame getImageFrameByImageGroupAndImageId(ImageGroup group,
                                                          long imageId);
    public List<ImageFrame> getFramesContainingImage(Image image);
    public List<ImageGroup> getAlbumsByOwnerScreenName(String screenName);
    public List<ImageGroup> getRollsByOwnerScreenName(String screenName);
    public List<User> getAlbumOwners();
    public List<User> getRollOwners();
    public List<User> getOwnersByType(String groupType);
    public List<ImageGroup> getByOwnerScreenNameAndType(String screenName,
                                                        String groupType);
    public ImageGroup getByOwnerNameAndType(User owner, String groupName,
                                            String groupType);
    public List<ImageGroup> getByImage(Image image);
    public ImageGroup getRollForImage(Image image);
    public ImageGroup getById(long id);
    public List<ImageGroup> getAll();
}
