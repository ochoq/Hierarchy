create table HIERARCHY_TEAM (
    ID varchar(36) not null,
    --
    NAME varchar(255),
    PARENT_ID varchar(36),
    --
    primary key (ID)
);