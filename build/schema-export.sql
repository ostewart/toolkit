alter table images drop constraint FKB95A82783BE47FC;
alter table images drop constraint FKB95A8278EB9DA075;
drop table lenses;
drop table images;
drop table cameras;
drop sequence id_sequence;
create table lenses (
   lens_id INT8 not null,
   name VARCHAR(255),
   manufacturer VARCHAR(255),
   focal_length INT4,
   min_aperature INT4,
   max_aperature INT4,
   primary key (lens_id)
);
create table images (
   image_id INT8 not null,
   type VARCHAR(255) not null,
   name varchar(100) not null,
   title varchar(1024) not null,
   caption varchar(4000),
   copyright varchar(1024),
   creator varchar(1024),
   notes TEXT,
   capture_date DATE,
   lens_id INT8,
   camera_id INT8,
   primary key (image_id)
);
create table cameras (
   camera_id INT8 not null,
   name VARCHAR(255),
   manufacturer VARCHAR(255),
   format VARCHAR(255),
   primary key (camera_id)
);
alter table images add constraint FKB95A82783BE47FC foreign key (lens_id) references lenses;
alter table images add constraint FKB95A8278EB9DA075 foreign key (camera_id) references cameras;
create sequence id_sequence;
