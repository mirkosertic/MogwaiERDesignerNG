/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.erdesignerng.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author p000010
 */
public class BooleanExtractor extends AbstractItemExtractor<Boolean>{

    public BooleanExtractor(String aColumnLabel) {
        super(aColumnLabel);
    }

    @Override
    public Boolean extractFrom(ResultSet aResultSet) throws SQLException {
        return aResultSet.getBoolean(getColumnLabel());
    }

}
