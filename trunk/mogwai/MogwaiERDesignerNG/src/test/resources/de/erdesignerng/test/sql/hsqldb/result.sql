CREATE SCHEMA SCHEMAA authorization SA;
CREATE SCHEMA SCHEMAB authorization SA;
CREATE TABLE SCHEMAA.TABLE1 (
	TB1_1 varchar(20) NOT NULL,
	TB1_2 varchar(100) DEFAULT 'Test',
	TB1_3 numeric(20,5) NOT NULL
);
ALTER TABLE SCHEMAA.TABLE1 ADD CONSTRAINT PK1 PRIMARY KEY(TB1_1);
CREATE INDEX SYS_IDX_10035 ON SCHEMAA.TABLE1 (TB1_1);
CREATE TABLE SCHEMAA.TABLE_2 (
	TB2_1 varchar(20) NOT NULL,
	TB2_2 varchar(100) DEFAULT 'Test',
	TB2_3 numeric(20,5) NOT NULL
);
ALTER TABLE SCHEMAA.TABLE_2 ADD CONSTRAINT PK3 PRIMARY KEY(TB2_1);
CREATE TABLE SCHEMAA.TABLEA2 (
	TB3_1 varchar(20) NOT NULL,
	TB3_2 varchar(100) DEFAULT 'Test',
	TB3_3 numeric(20,5) NOT NULL
);
ALTER TABLE SCHEMAA.TABLEA2 ADD CONSTRAINT PK6 PRIMARY KEY(TB3_1);
CREATE TABLE SCHEMAB.TABLE1 (
	TB2_1 varchar(20) NOT NULL,
	TB2_2 varchar(100) DEFAULT 'A',
	TB2_3 numeric(20,5) NOT NULL
);
CREATE UNIQUE INDEX TABL11_IDX1 ON SCHEMAB.TABLE1 (TB2_2);
CREATE INDEX TABL11_IDX2 ON SCHEMAB.TABLE1 (TB2_3);
CREATE INDEX SYS_IDX_10047 ON SCHEMAB.TABLE1 (TB2_1);
CREATE TABLE SCHEMAB.TABLE2 (
	TB3_1 varchar(20) NOT NULL,
	TB3_2 varchar(100) DEFAULT 'A',
	TB3_3 numeric(20,5) NOT NULL
);
ALTER TABLE SCHEMAB.TABLE2 ADD CONSTRAINT PK2 PRIMARY KEY(TB3_1);
CREATE TABLE SCHEMAB.TABLE_5 (
	TB5_1 varchar(20) NOT NULL,
	TB5_2 varchar(100) DEFAULT 'Test',
	TB5_3 numeric(20,5) NOT NULL
);
ALTER TABLE SCHEMAB.TABLE_5 ADD CONSTRAINT PK8 PRIMARY KEY(TB5_1);
CREATE VIEW SCHEMAB.VIEW1 AS SELECT SCHEMAA.TABLE1.TB1_1,SCHEMAA.TABLE1.TB1_2,SCHEMAA.TABLE1.TB1_3 FROM SCHEMAA.TABLE1;
ALTER TABLE SCHEMAA.TABLE1 ADD CONSTRAINT FK1 FOREIGN KEY (TB1_1) REFERENCES SCHEMAA.TABLE_2(TB2_1) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE SCHEMAB.TABLE1 ADD CONSTRAINT ERRELSYS_0 FOREIGN KEY (TB2_1) REFERENCES SCHEMAB.TABLE2(TB3_1) ON DELETE NO ACTION ON UPDATE NO ACTION;