INSERT INTO users (user_id, screen_name, primary_email, first_name, last_name,
                   password)
            VALUES (nextval('id_sequence'), 'oliver', 'oliver@trailmagic.com',
                    'Oliver', 'Stewart', '5f4dcc3b5aa765d61d8327deb882cf99');

INSERT INTO groups (group_id, name, owner_id)
VALUES (nextval('id_sequence'), 'everyone', (select user_id from users where screen_name = 'oliver'));

INSERT INTO groups_users (group_id, user_id)
VALUES ((select group_id from groups where name = 'everyone'), (select user_id from users where screen_name = 'oliver'));

INSERT INTO image_groups (group_id, supergroup_id, name, display_name,
                          type, description, owner_id)
VALUES (nextval('id_sequence'), currval('id_sequence'), 'test', 'Test Roll', 'roll', '',
        (select user_id from users where screen_name = 'oliver'));

INSERT INTO images (image_id, name, displayName, caption, copyright,
                    creator, owner_id, cd_id)
VALUES (nextval('id_sequence'), 'bug-love', 'Bug Love', 'Tender Bug Love',
        'Copyright 2004', 'Oliver Stewart',
        (select user_id from users where screen_name = 'oliver'),
        null);

INSERT INTO photos (image_id, notes, capture_date, lens_id, camera_id,
                    image_group_id)
VALUES ((select image_id from images where name = 'bug-love'),
        '', null, null, null,
        (select group_id from image_groups where name = 'test'));

INSERT INTO image_frames (frame_id, group_id, position, caption, image_id)
VALUES (nextval('id_sequence'),
        (select group_id from image_groups where name = 'test'),
        0, 'Good Stuff',
        (select image_id from images where name = 'bug-love'));

INSERT INTO image_groups (group_id, supergroup_id, name, display_name,
                          type, description, owner_id)
VALUES (nextval('id_sequence'), currval('id_sequence'), 'test-album', 'Test Album', 'album', '',
        (select user_id from users where screen_name = 'oliver'));

INSERT INTO image_frames (frame_id, group_id, position, caption, image_id)
VALUES (nextval('id_sequence'),
        (select group_id from image_groups where name = 'test-album'),
        0, 'Good Stuff',
        (select image_id from images where name = 'bug-love'));



INSERT INTO images (image_id, name, displayName, caption, copyright,
                    creator, owner_id, cd_id)
VALUES (nextval('id_sequence'), 'oliver-suit', 'Fancy', 'Spiffy, Eh?',
        'Copyright 2004', 'Oliver Stewart',
        (select user_id from users where screen_name = 'oliver'),
        null);

INSERT INTO photos (image_id, notes, capture_date, lens_id, camera_id,
                    image_group_id)
VALUES ((select image_id from images where name = 'oliver-suit'),
        'No notes', '2004-02-14 00:00:00', null, null,
        (select group_id from image_groups where name = 'test'));

INSERT INTO image_frames (frame_id, group_id, position, caption, image_id)
VALUES (nextval('id_sequence'),
        (select group_id from image_groups where name = 'test-album'),
        1, 'Nice Suit',
        (select image_id from images where name = 'oliver-suit'));
