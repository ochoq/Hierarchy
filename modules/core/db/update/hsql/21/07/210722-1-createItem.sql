create table HIERARCHY_ITEM (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TAG varchar(255) not null,
    NAME varchar(255),
    WORKLOAD integer,
    PARENT_ID varchar(36),
    --
    primary key (ID)
);