create schema schemaa
create schema schemab
create table schemaa.Table1 (tb1_1 varchar(20) not null,tb1_2 varchar(100) default 'Test',tb1_3 numeric(20,5) not null,CONSTRAINT PK1 PRIMARY KEY (tb1_1))
create table schemab.Table1 (tb2_1 varchar(20) not null,tb2_2 varchar(100) default 'A' ,tb2_3 numeric(20,5) not null)
create view schemab.View1 as SELECT * from schemab.Table1
create unique index Tabl11_idx1 on schemab.Table1 (tb2_2)
create index Tabl11_idx2 on schemab.Table1 (tb2_3)
comment on table schemaa.Table1 is 'Tablecomment'
comment on column schemaa.Table1.tb1_1 is 'Columncomment'