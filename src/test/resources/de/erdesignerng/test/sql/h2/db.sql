create schema schemaa authorization DBA
create schema schemab authorization DBA
set schema schemaa
create table Table1 (tb1_1 varchar(20) not null,tb1_2 varchar(100) default 'Test',tb1_3 numeric(20,5) not null,CONSTRAINT PK1 PRIMARY KEY (tb1_1))
set schema schemab
create table Table1 (tb2_1 varchar(20) not null,tb2_2 varchar(100) default 'A' ,tb2_3 numeric(20,5) not null)
create view View1 as SELECT * from Table1;
create unique index Tabl11_idx1 on Table1 (tb2_2);
create index Tabl11_idx2 on Table1 (tb2_3);
