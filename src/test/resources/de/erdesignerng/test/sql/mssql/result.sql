CREATE TABLE Table_2 (
    tb2_1 varchar(20) NOT NULL,
    tb2_2 varchar(100) DEFAULT ('Test'),
    tb2_3 decimal(20,5) NOT NULL
) ON "default";
ALTER TABLE Table_2 ADD CONSTRAINT PK3 PRIMARY KEY(tb2_1);
CREATE TABLE Table1 (
    tb2_1 varchar(20) NOT NULL,
    tb2_2 varchar(100) DEFAULT ('A'),
    tb2_3 decimal(20,5) NOT NULL
) ON "default";
CREATE UNIQUE INDEX Tabl11_idx1 ON Table1 (tb2_2) ON "default";
CREATE INDEX Tabl11_idx2 ON Table1 (tb2_3) ON "default";
CREATE TABLE Table2 (
    tb3_1 varchar(20) NOT NULL,
    tb3_2 varchar(100) DEFAULT ('A'),
    tb3_3 decimal(20,5) NOT NULL
) ON "default";
ALTER TABLE Table2 ADD CONSTRAINT PK2 PRIMARY KEY(tb3_1);
CREATE TABLE Tablea2 (
    tb3_1 varchar(20) NOT NULL,
    tb3_2 varchar(100) DEFAULT ('Test'),
    tb3_3 decimal(20,5) NOT NULL
) ON "default";
ALTER TABLE Tablea2 ADD CONSTRAINT PK6 PRIMARY KEY(tb3_1);
CREATE VIEW View1 AS SELECT * from Table1;
ALTER TABLE Table1 ADD CONSTRAINT FK18 FOREIGN KEY (tb2_1) REFERENCES Table_2(tb2_1) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE Table1 ADD CONSTRAINT FK1 FOREIGN KEY (tb2_1) REFERENCES Table2(tb3_1) ON DELETE NO ACTION ON UPDATE NO ACTION;