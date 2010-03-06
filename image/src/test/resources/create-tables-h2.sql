drop table image_manifestations;
drop table image_frames;
drop table images;
drop table image_groups;
drop table photos;
drop table groups_users;
drop table groups;
drop table users;
drop table image_cds;
drop table cameras;
drop table lenses;



CREATE TABLE users (
  user_id identity primary key,
  screen_name varchar(32) NOT NULL,
  primary_email varchar(1024) NOT NULL,
  first_name varchar(1024) NOT NULL,
  last_name varchar(1024) NOT NULL,
  password varchar(32) NOT NULL,
  UNIQUE (screen_name)
);

CREATE TABLE groups (
  group_id identity primary key,
  name varchar(255) NOT NULL,
  owner_id bigint references users(user_id),
  UNIQUE (name)
);

CREATE TABLE groups_users (
  group_id identity primary key,
  user_id bigint NOT NULL references users(user_id)
);


CREATE TABLE cameras (
  camera_id identity primary key,
  name varchar(255) NOT NULL,
  manufacturer varchar(255) NOT NULL,
  format varchar(255) NOT NULL
);

CREATE TABLE lenses (
  lens_id identity primary key,
  name varchar(255) NOT NULL,
  manufacturer varchar(255) NOT NULL,
  focal_length int NOT NULL,
  min_aperture int(11) NOT NULL,
  max_aperture int(11) NOT NULL,
);

CREATE TABLE image_cds (
  cd_id identity primary key,
  number int NOT NULL,
  description clob NOT NULL,
  UNIQUE (number)
);

CREATE TABLE images (
  image_id identity primary key,
  name varchar(100) NOT NULL,
  display_name varchar(2048) NOT NULL default '',
  caption clob,
  copyright varchar(2048),
  creator varchar(2048),
  number int,
  owner_id bigint NOT NULL references users(user_id),
  cd_id bigint references image_cds(cd_id)
);

CREATE TABLE image_groups (
  group_id identity primary key,
  supergroup_id bigint references image_groups(group_id),
  name varchar(100) NOT NULL,
  display_name varchar(2048) NOT NULL,
  type varchar(100) NOT NULL,
  description clob,
  owner_id bigint references users(user_id),
  upload_date timestamp,
  preview_image bigint references images(image_id),
  UNIQUE (owner_id, name, type)
);

CREATE TABLE photos (
  image_id identity primary key,
  notes clob,
  capture_date date,
  lens_id bigint references lenses(lens_id),
  camera_id bigint references cameras(camera_id),
  image_group_id bigint references image_groups(group_id)
);

CREATE TABLE image_frames (
  frame_id identity primary key,
  group_id bigint NOT NULL references image_groups(group_id),
  caption clob,
  image_id bigint NOT NULL references images(image_id),
  pos int NOT NULL,
  UNIQUE (pos, group_id)
);

CREATE TABLE image_manifestations (
  manifestation_id identity primary key,
  image_id bigint(20) NOT NULL references images(image_id),
  height int NOT NULL,
  width int NOT NULL,
  format varchar(1024) NOT NULL,
  originalp boolean NOT NULL,
  name varchar(2048),
  image_data blob NOT NULL
);
