package com.trailmagic.image;

import com.trailmagic.user.User;
import java.util.List;

public interface ImageGroupFactory {
    public int ROLL_TYPE = 0;
    public int ALBUM_TYPE = 1;

    public ImageGroup newInstance(int type);
    public ImageGroup getAlbumByOwnerAndName(User owner, String name);
    public ImageGroup getRollByOwnerAndName(User owner, String name);
    public ImageFrame getImageFrameByImageGroupAndImageId(ImageGroup album,
                                                          long imageId);
    public List getAlbumsByOwnerScreenName(String name);
    public List getAlbumOwners();
}
