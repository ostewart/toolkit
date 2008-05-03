-- All photos whose parent is an ImageFrame:

select frames.id as frame_id, frames.object_identity as frame, photos.id as photo_id, photos.object_identity as photo from acl_object_identity as photos inner join acl_object_identity as frames on photos.parent_object = frames.id where frames.object_identity like 'com.trailmagic.image.ImageFrame%' and photos.object_identity like 'com.trailmagic.image.Photo%';

