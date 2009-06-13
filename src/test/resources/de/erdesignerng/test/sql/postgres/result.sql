CREATE SCHEMA schemaa;
CREATE SCHEMA schemab;
CREATE TABLE schemaa.table1 (
    tb1_1 varchar(20) NOT NULL,
    tb1_2 varchar(100) DEFAULT 'Test'::character varying,
    tb1_3 numeric(20,5) NOT NULL
);
ALTER TABLE schemaa.table1 ADD CONSTRAINT pk1 PRIMARY KEY(tb1_1);
CREATE TABLE schemab.table1 (
    tb2_1 varchar(20) NOT NULL,
    tb2_2 varchar(100) DEFAULT 'A'::character varying,
    tb2_3 numeric(20,5) NOT NULL
);
CREATE UNIQUE INDEX tabl11_idx1 ON schemab.table1 (tb2_2);
CREATE INDEX tabl11_idx2 ON schemab.table1 (tb2_3);
CREATE TABLE schemab.table2 (
    tb3_1 varchar(20) NOT NULL,
    tb3_2 varchar(100) DEFAULT 'A'::character varying,
    tb3_3 numeric(20,5) NOT NULL
);
CREATE INDEX tabl22_idx3 ON schemab.table2 (upper((tb3_2)::text));
CREATE VIEW schemab.view1 AS SELECT table1.tb2_1, table1.tb2_2, table1.tb2_3 FROM schemab.table1;;
ALTER TABLE schemab.table1 ADD CONSTRAINT fk1 FOREIGN KEY (tb2_1) REFERENCES schemaa.table1(tb1_1) ON DELETE NO ACTION ON UPDATE NO ACTION;