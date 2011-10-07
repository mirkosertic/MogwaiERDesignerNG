/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.visual;

import org.apache.commons.lang.SystemUtils;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to collect session based usage data.
 */
public class UsageDataCollector {

    public static enum Usecase {
        ADD_RELATION, ADD_TABLE, ADD_COMMENT, ADD_VIEW, EDIT_CLASSPATH, COMPLETE_COMPARE_WITH_DATABASE, COMPLETE_COMPARE_WITH_MODEL, CONVERT_MODEL, DATABROWSER, EDIT_DB_CONNECTION, EDIT_CUSTOM_TYPES, EDIT_DOMAINS, EDIT_RELATION, EDIT_SUBJECT_AREA, EDIT_TABLE, EDIT_VIEW, GENERATE_CHANGELOG, GENERATE_DOCUMENTATION, GENERATE_MIGRATION_SCRIPT, GENERATE_COMPLETE_SQL, PERFORM_MODEL_CHECK, OPEN_FROM_FILE, OPEN_FROM_REPOSITORY, OPENXAVA_EXPORT, EDIT_REPOSITORY, REVERSE_ENGINEER, SAVE_TO_FILE, SAVE_TO_REPOSITORY, SHOW_HIDE_RELATONS_FOR_TABLE, EDITOR_DIAGRAM_MODE, REMOVE_SUBJECT_AREA, SET_DISPLAY_LEVEL, SET_DISPLAY_ORDER, HIDE_SUBJECT_AREA, SHOW_SUBJECT_AREA, USE_TOOL_HAND, USE_TOOL_ENTITY, USE_TOOL_RELATION, USE_TOOL_VIEW, USE_TOOL_COMMENT, SET_ZOOMLEVEL, ADD_TO_NEW_SUBJECTAREA, CHANGE_DISPLAY_COMMENTS_STATE, CHANGE_DISPLAY_GRID_STATE, SET_INTELLIGENT_LAYOUT_STATE, CHANGE_INTELLIGENT_LAYOUT_STATE, DELETE_MODEL_ELEMENT, LAYOUT_CLUSTER, LAYOUT_TREE, LAYOUT_RADIAL, LAYOUT_GRID, LAYOUT_SELF_ORGANIZING, LAYOUT_ORGANIC, LAYOUT_FAST_ORGANIC, LAYOUT_RADIAL_TREE, LAYOUT_TREE2, LAYOUT_HIERARCHICAL, ZOOM_IN, ZOOM_OUT, EDITOR_2DINTERACTIVE_MODE, EDITOR_3DINTERACTIVE_MODE;
    }

    private class UsageData {
        int counter;
    }

    private class DatabaseConnectionInfo {
        String databaseProductName;
        String databaseProductVersion;
        String driverName;
        String driverVersion;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DatabaseConnectionInfo that = (DatabaseConnectionInfo) o;

            if (databaseProductName != null ? !databaseProductName.equals(that.databaseProductName) : that.databaseProductName != null)
                return false;
            if (databaseProductVersion != null ? !databaseProductVersion.equals(that.databaseProductVersion) : that.databaseProductVersion != null)
                return false;
            if (driverName != null ? !driverName.equals(that.driverName) : that.driverName != null) return false;
            if (driverVersion != null ? !driverVersion.equals(that.driverVersion) : that.driverVersion != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = databaseProductName != null ? databaseProductName.hashCode() : 0;
            result = 31 * result + (databaseProductVersion != null ? databaseProductVersion.hashCode() : 0);
            result = 31 * result + (driverName != null ? driverName.hashCode() : 0);
            result = 31 * result + (driverVersion != null ? driverVersion.hashCode() : 0);
            return result;
        }
    }

    private static UsageDataCollector ME;

    private long createdTimestamp;
    private Map<Usecase, UsageData> executedUsecase = new HashMap<Usecase, UsageData>();
    private Set<DatabaseConnectionInfo> connectedDatabases = new HashSet<DatabaseConnectionInfo>();

    public static synchronized UsageDataCollector getInstance() {
        if (ME == null) {
            ME = new UsageDataCollector();
        }
        return ME;
    }

    private UsageDataCollector() {
        createdTimestamp = System.currentTimeMillis();
    }

    public void initialize() {
    }

    public void addExecutedUsecase(Usecase aCase) {
        UsageData theData = executedUsecase.get(aCase);
        if (theData != null) {
            theData.counter++;
        } else {
            theData = new UsageData();
            theData.counter = 1;
            executedUsecase.put(aCase, theData);
        }
    }

    public void addConnectedDatabase(DatabaseMetaData aMetaData) throws SQLException {
        DatabaseConnectionInfo theInfo = new DatabaseConnectionInfo();
        theInfo.databaseProductName = aMetaData.getDatabaseProductName();
        theInfo.databaseProductVersion = aMetaData.getDatabaseProductVersion();
        theInfo.driverName = aMetaData.getDriverName();
        theInfo.driverVersion = aMetaData.getDriverVersion();

        connectedDatabases.add(theInfo);
    }


    public void getHTMLSummary(StringBuilder aBuilder) {

        long theDuration = (System.currentTimeMillis() - createdTimestamp) / 1000 / 60;

        aBuilder.append("<br/>");
        aBuilder.append("<br/>");
        aBuilder.append("<b>User language&nbsp;:&nbsp;</b>");
        aBuilder.append(SystemUtils.USER_LANGUAGE);
        aBuilder.append("<br/>");
        aBuilder.append("<b>User country&nbsp;:&nbsp;</b>");
        aBuilder.append(SystemUtils.USER_COUNTRY);
        aBuilder.append("<br/>");
        aBuilder.append("<b>User timezone&nbsp;:&nbsp;</b>");
        aBuilder.append(SystemUtils.USER_TIMEZONE);
        aBuilder.append("<br/>");
        aBuilder.append("<b>Operating System&nbsp;:&nbsp;</b>");
        aBuilder.append(SystemUtils.OS_NAME);
        aBuilder.append("&nbsp;");
        aBuilder.append(SystemUtils.OS_VERSION);
        aBuilder.append("&nbsp;");
        aBuilder.append(SystemUtils.OS_ARCH);
        aBuilder.append("<br/>");
        aBuilder.append("<b>Java Specification&nbsp;:&nbsp;</b>");
        aBuilder.append(SystemUtils.JAVA_SPECIFICATION_VENDOR);
        aBuilder.append("&nbsp;");
        aBuilder.append(SystemUtils.JAVA_SPECIFICATION_NAME);
        aBuilder.append("&nbsp;");
        aBuilder.append(SystemUtils.JAVA_SPECIFICATION_VERSION);
        aBuilder.append("<br/>");
        aBuilder.append("<b>Java Runtime&nbsp;:&nbsp;</b>");
        aBuilder.append(SystemUtils.JAVA_RUNTIME_NAME);
        aBuilder.append("&nbsp;");
        aBuilder.append(SystemUtils.JAVA_RUNTIME_VERSION);
        aBuilder.append("<br/>");
        aBuilder.append("<b>Usage Time&nbsp;:&nbsp;</b>");
        aBuilder.append(theDuration);
        aBuilder.append("&nbsp;minutes");
        aBuilder.append("<br/>");
        aBuilder.append("<br/>");
        aBuilder.append("<br/>");
        aBuilder.append("<table><tr><td><b>Executed Usecases</b></td><td></td></tr>");
        for (Map.Entry<Usecase, UsageData> theEntry : executedUsecase.entrySet()) {
            aBuilder.append("<tr><td>");
            aBuilder.append(theEntry.getKey());
            aBuilder.append("</td><td>");
            aBuilder.append(theEntry.getValue().counter);
            aBuilder.append("&nbsp;time(s)");
            aBuilder.append("</<td></tr>");
        }
        aBuilder.append("</table>");
        aBuilder.append("<br/>");
        aBuilder.append("<table><tr><td><b>Database Product</b></td><td><b>Database Version</b></td><td><b>Driver name</b></td><td><b>Driver Version</b></td></tr>");
        for (DatabaseConnectionInfo theEntry : connectedDatabases) {
            aBuilder.append("<tr><td>");
            aBuilder.append(theEntry.databaseProductName);
            aBuilder.append("</td></td>");
            aBuilder.append(theEntry.databaseProductVersion);
            aBuilder.append("</td></td>");
            aBuilder.append(theEntry.driverName);
            aBuilder.append("</td></td>");
            aBuilder.append(theEntry.driverVersion);
            aBuilder.append("</td></tr>");
        }
        aBuilder.append("</table>");
    }

    public void flush() {
    }
}