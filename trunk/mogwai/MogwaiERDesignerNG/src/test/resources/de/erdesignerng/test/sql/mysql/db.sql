create table Table1 (tb2_1 varchar(20) not null,tb2_2 varchar(100) default 'A' ,tb2_3 decimal(20,5) not null)
create table Table2 (tb3_1 varchar(20) not null,tb3_2 varchar(100) default 'A' ,tb3_3 decimal(20,5) not null)
create view View1 as SELECT * from Table1
create unique index Tabl11_idx1 on Table1 (tb2_2)
alter table Table2 add constraint PK2 primary key (tb3_1)
create index Tabl11_idx2 on Table1 (tb2_3)
alter table Table1 add constraint FK1 foreign key (tb2_1) references Table2(tb3_1) on delete no action on update no action;