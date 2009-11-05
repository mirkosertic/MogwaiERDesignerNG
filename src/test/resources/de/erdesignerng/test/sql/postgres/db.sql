create schema schemaa
create schema schemab
create domain TestDomain varchar(20);
create table schemaa.Table1 (tb1_1 TestDomain not null,tb1_2 varchar(100) default 'Test',tb1_3 numeric(20,5) not null,CONSTRAINT PK1 PRIMARY KEY (tb1_1))
create table schemab.Table1 (tb2_1 varchar not null,tb2_2 varchar(100) default 'A' ,tb2_3 numeric(20,5) not null)
create table schemab.Table2 (tb3_1 varchar(20) not null,tb3_2 varchar(100) default 'A' ,tb3_3 numeric(20,5) not null)
create view schemab.View1 as SELECT * from schemab.Table1
create unique index Tabl11_idx1 on schemab.Table1 (tb2_2)
create index Tabl11_idx2 on schemab.Table1 (tb2_3)
comment on table schemaa.Table1 is 'Tablecomment'
comment on column schemaa.Table1.tb1_1 is 'Columncomment'
create index Tabl22_idx3 on schemab.Table2 (upper(tb3_2))
alter table schemab.Table1 add constraint FK1 foreign key (tb2_1) references schemaa.Table1(tb1_1) on delete no action on update no action