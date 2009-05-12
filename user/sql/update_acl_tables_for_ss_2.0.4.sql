create table acl_sid (
  id bigint not null auto_increment primary key,
  principal boolean not null,
  sid varchar(100) not null,
  unique(sid,principal) );

create table acl_class (
  id bigint not null auto_increment primary key,
  class varchar(100) not null,
  unique(class) );

alter table acl_object_identity rename to acl_object_identity_old;

create table acl_object_identity (
  id bigint not null auto_increment primary key,
  object_id_class bigint not null references acl_class(id),
  object_id_identity bigint not null,
  parent_object bigint references acl_object_identity(id),
  owner_sid bigint references acl_sid(id),
  entries_inheriting boolean not null,
  unique(object_id_class, object_id_identity));

insert into acl_sid (id, principal, sid) values (1, true, 'oliver');
insert into acl_sid (id, principal, sid) values (2, false, 'ROLE_EVERYONE');

insert into acl_class (class)
select distinct substr(object_identity, 1, locate(':', object_identity)-1) as class
from acl_object_identity_old;


-- this probably needs more rows with the owner for everyone
insert into acl_object_identity
select acl_object_identity_old.id as id,
       acl_class.id as object_id_class,
       substr(object_identity, locate(':', object_identity)+1) as object_id_identity,
       parent_object,
       1,
       false
from acl_object_identity_old, acl_class
where acl_class.class = substr(object_identity, 1, locate(':', object_identity)-1);

create table acl_entry (
  id bigint not null auto_increment primary key,
  acl_object_identity bigint not null references acl_object_identity(id),
  ace_order int not null ,
  sid bigint not null references acl_sid(id),
  mask integer not null,
  granting boolean not null,
  audit_success boolean not null,
  audit_failure boolean not null,
  unique(acl_object_identity,ace_order)
);


insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select acl_object_identity, 0, 1, 1, true, false, false from acl_permission where recipient = 'oliver' and mask = 31;

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select acl_object_identity, 1, 1, 2, true, false, false from acl_permission where recipient = 'oliver' and mask = 31;

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select acl_object_identity, 2, 1, 4, true, false, false from acl_permission where recipient = 'oliver' and mask = 31;

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select acl_object_identity, 3, 1, 8, true, false, false from acl_permission where recipient = 'oliver' and mask = 31;

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select acl_object_identity, 4, 1, 16, true, false, false from acl_permission where recipient = 'oliver' and mask = 31;


insert into acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select id, acl_object_identity, 5, 2, 1, true, false, false from acl_permission where recipient = 'ROLE_EVERYONE' and mask = 2;

insert into acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select id, acl_object_identity, 5, 2, 2, true, false, false from acl_permission where recipient = 'ROLE_EVERYONE' and mask = 4;

insert into acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select id, acl_object_identity, 5, 2, 4, true, false, false from acl_permission where recipient = 'ROLE_EVERYONE' and mask = 8;

insert into acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select id, acl_object_identity, 5, 2, 8, true, false, false from acl_permission where recipient = 'ROLE_EVERYONE' and mask = 16;

insert into acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
        select id, acl_object_identity, 5, 2, 16, true, false, false from acl_permission where recipient = 'ROLE_EVERYONE' and mask = 1;


