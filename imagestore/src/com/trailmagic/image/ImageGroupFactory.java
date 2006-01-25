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
    public List getAlbumsByOwnerScreenName(String screenName);
    public List getAlbumOwners();
    public List getRollsByOwnerScreenName(String screenName);
    public List getRollOwners();
    public List getOwnersByType(String groupType);
    public List getByOwnerScreenNameAndType(String screenName,
                                            String groupType);
    public ImageGroup getByOwnerNameAndType(User owner, String groupName,
                                            String groupType);
    public List getByImage(Image image);
    public ImageGroup getRollForImage(Image image);
}
