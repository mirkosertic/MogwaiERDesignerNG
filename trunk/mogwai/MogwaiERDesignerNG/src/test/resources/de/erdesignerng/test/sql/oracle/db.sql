create table Table1 (tb2_1 varchar2(20) not null,tb2_2 varchar2(100) default 'A' ,tb2_3 number(20,5) not null, tb2_4 number(9,0))
create table Table2 (tb3_1 varchar2(20) not null,tb3_2 varchar2(100) default 'A' ,tb3_3 number(20,5) not null)
create table Table_2 (tb2_1 varchar(20) not null,tb2_2 varchar(100) default 'Test',tb2_3 numeric(20,5) not null)
alter table Table_2 add constraint PK8 primary key (tb2_1)
create table Tablea2 (tb3_1 varchar(20) not null,tb3_2 varchar(100) default 'Test',tb3_3 numeric(20,5) not null)
alter table Tablea2 add constraint PK9 primary key (tb3_1)
alter table Table1 add constraint FK1 foreign key (tb1_1) references Table_2(tb2_1) on delete no action on update no action;
create view View1 as SELECT * from Table1
create unique index Tabl11_idx1 on Table1 (tb2_2)
alter table Table2 add constraint PK2 primary key (tb3_1)
create index Tabl11_idx2 on Table1 (tb2_3)
create index Tabl11_idx3 on Table1 (lower(tb2_2))
alter table Table1 add constraint FK1 foreign key (tb2_1) references Table2(tb3_1)