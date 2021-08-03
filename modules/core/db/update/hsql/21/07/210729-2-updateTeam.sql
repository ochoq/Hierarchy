alter table HIERARCHY_TEAM alter column PARENT_ID rename to PARENT_ID__U10862 ^
alter table HIERARCHY_TEAM drop constraint FK_HIERARCHY_TEAM_ON_PARENT ;
drop index IDX_HIERARCHY_TEAM_ON_PARENT ;
alter table HIERARCHY_TEAM add column TRIBE varchar(255) ;
alter table HIERARCHY_TEAM add column PLATFORM varchar(255) ;
