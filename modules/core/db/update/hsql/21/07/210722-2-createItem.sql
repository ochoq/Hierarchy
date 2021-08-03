alter table HIERARCHY_ITEM add constraint FK_HIERARCHY_ITEM_ON_PARENT foreign key (PARENT_ID) references HIERARCHY_ITEM(ID);
create unique index IDX_HIERARCHY_ITEM_UNIQ_TAG on HIERARCHY_ITEM (TAG);
create index IDX_HIERARCHY_ITEM_ON_PARENT on HIERARCHY_ITEM (PARENT_ID);
