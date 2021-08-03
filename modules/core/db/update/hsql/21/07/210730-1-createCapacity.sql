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
);