alter table HIERARCHY_CAPACITY add constraint FK_HIERARCHY_CAPACITY_ON_TEAM foreign key (TEAM_ID) references HIERARCHY_TEAM(ID);
create index IDX_HIERARCHY_CAPACITY_ON_TEAM on HIERARCHY_CAPACITY (TEAM_ID);
