package de.erdesignerng.dialect.generic;

import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;

public class GenericSQLGenerator extends SQL92SQLGenerator<GenericJDBCDialect> {

    public GenericSQLGenerator(GenericJDBCDialect aDialect) {
        super(aDialect);
    }
}