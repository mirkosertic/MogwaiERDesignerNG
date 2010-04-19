create schema schemaa authorization SA
create schema schemab authorization SA
set schema schemaa
create table Table1 (tb1_1 varchar(20) not null,tb1_2 varchar(100) default 'Test',tb1_3 numeric(20,5) not null,CONSTRAINT PK1 PRIMARY KEY (tb1_1))
create table Table_2 (tb2_1 varchar(20) not null,tb2_2 varchar(100) default 'Test',tb2_3 numeric(20,5) not null,CONSTRAINT PK3 PRIMARY KEY (tb2_1))
create table Tablea2 (tb3_1 varchar(20) not null,tb3_2 varchar(100) default 'Test',tb3_3 numeric(20,5) not null,CONSTRAINT PK6 PRIMARY KEY (tb3_1))
alter table Table1 add constraint FK1 foreign key (tb1_1) references Table_2(tb2_1) on delete no action on update no action;
set schema schemab
create table Table1 (tb2_1 varchar(20) not null,tb2_2 varchar(100) default 'A' ,tb2_3 numeric(20,5) not null)
create table Table2 (tb3_1 varchar(20) not null,tb3_2 varchar(100) default 'A' ,tb3_3 numeric(20,5) not null)
create table Table_5 (tb5_1 varchar(20) not null,tb5_2 varchar(100) default 'Test',tb5_3 numeric(20,5) not null,CONSTRAINT PK8 PRIMARY KEY (tb5_1))
create view View1 as SELECT * FROM schemaa.Table1 
create unique index Tabl11_idx1 on Table1 (tb2_2)
alter table Table2 add constraint PK2 primary key (tb3_1)
create index Tabl11_idx2 on Table1 (tb2_3)
alter table Table1 add constraint FK1 foreign key (tb2_1) references Table2(tb3_1) on delete no action on update no action;