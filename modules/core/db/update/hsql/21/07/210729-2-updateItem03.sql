alter table HIERARCHY_ITEM add constraint FK_HIERARCHY_ITEM_ON_PARENT foreign key (PARENT_ID) references HIERARCHY_ITEM(ID) on delete CASCADE;
