-- begin HIERARCHY_TEAM
create table HIERARCHY_TEAM (
    ID varchar(36) not null,
    --
    PLATFORM varchar(255),
    TRIBE varchar(255),
    NAME varchar(255),
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end HIERARCHY_TEAM
-- begin HIERARCHY_ITEM
create table HIERARCHY_ITEM (
    ID varchar(36) not null,
    --
    SMART_ID varchar(255),
    YEAR_ varchar(255),
    TAG varchar(255) not null,
    TYPE_ varchar(50),
    NAME varchar(255),
    TEAM_ID varchar(36),
    TSHIRT integer,
    WORKLOAD integer,
    Q1 integer,
    Q2 integer,
    Q3 integer,
    Q4 integer,
    PARENT_ID varchar(36),
    EMPTY varchar(1),
    --
    primary key (ID)
)^
-- end HIERARCHY_ITEM
-- begin HIERARCHY_COUNTER
create table HIERARCHY_COUNTER (
    ID varchar(36) not null,
    --
    ITEM_COUNTER integer,
    --
    primary key (ID)
)^
-- end HIERARCHY_COUNTER
-- begin HIERARCHY_CAPACITY
create table HIERARCHY_CAPACITY (
    ID varchar(36) not null,
    --
    YEAR_ varchar(255),
    TEAM_ID varchar(36),
    Q1 integer,
    Q2 integer,
    Q3 integer,
    Q4 integer,
    --
    primary key (ID)
)^
-- end HIERARCHY_CAPACITY
