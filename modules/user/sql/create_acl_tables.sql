CREATE TABLE acl_object_identity (
     id BIGINT NOT NULL AUTO_INCREMENT,
     object_identity VARCHAR(250) NOT NULL,
     parent_object BIGINT,
     acl_class VARCHAR(250) NOT NULL,
        primary key (id),
     UNIQUE(object_identity),
     FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id)
);

CREATE TABLE acl_permission (
     id BIGINT NOT NULL AUTO_INCREMENT,
     acl_object_identity BIGINT NOT NULL,
     recipient VARCHAR(100) NOT NULL,
     mask INTEGER NOT NULL,
        primary key (id),
     UNIQUE(acl_object_identity, recipient),
     FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id)
);