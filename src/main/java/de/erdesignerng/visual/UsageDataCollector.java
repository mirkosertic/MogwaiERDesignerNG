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

import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.MavenPropertiesLocator;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

/**
 * Class to collect session based usage data.
 */
public class UsageDataCollector {

    private static final Logger LOGGER = Logger.getLogger(UsageDataCollector.class);

    private static final String POST_URL = "http://mogwai.sourceforge.net/usagecollector.php";

    private static final String ROW_SEPARATOR = "\n";

    private static final String COLUMN_SEPARATOR = "|";

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


    public void getHTMLSummary(StringBuilder aBuilder, boolean aDemoMode) {

        long theDuration = (System.currentTimeMillis() - createdTimestamp) / 1000 / 60;

        aBuilder.append("<br/>");
        aBuilder.append("<br/>");
        aBuilder.append("<b>ERDesignerNG installation ID&nbsp;:&nbsp;</b>");
        aBuilder.append(ApplicationPreferences.getInstance().getInstallationId());
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
        if (!aDemoMode) {
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
                aBuilder.append("</td></tr>");
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
    }

    public void flush() {
        try {

            long theDuration = (System.currentTimeMillis() - createdTimestamp) / 1000 / 60;

            LOGGER.info("Connecting to " + POST_URL);

            StringBuilder theBuilder = new StringBuilder();
            // Session information
            theBuilder.append("S").append(COLUMN_SEPARATOR).append(ApplicationPreferences.getInstance().getInstallationId()).append(COLUMN_SEPARATOR).append("Mogwai ERDesignerNG ").append(MavenPropertiesLocator.getERDesignerVersionInfo()).append(ROW_SEPARATOR);
            // Properties
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("Usage Time In Minutes").append(COLUMN_SEPARATOR).append(theDuration).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("User Language").append(COLUMN_SEPARATOR).append(SystemUtils.USER_LANGUAGE).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("User Country").append(COLUMN_SEPARATOR).append(SystemUtils.USER_COUNTRY).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("User Timezone").append(COLUMN_SEPARATOR).append(SystemUtils.USER_TIMEZONE).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("OS Name").append(COLUMN_SEPARATOR).append(SystemUtils.OS_NAME).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("OS Version").append(COLUMN_SEPARATOR).append(SystemUtils.OS_VERSION).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("OS Arch").append(COLUMN_SEPARATOR).append(SystemUtils.OS_ARCH).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("Java Spec Vendor").append(COLUMN_SEPARATOR).append(SystemUtils.JAVA_SPECIFICATION_VENDOR).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("Java Spec Name").append(COLUMN_SEPARATOR).append(SystemUtils.JAVA_SPECIFICATION_NAME).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("Java Spec Version").append(COLUMN_SEPARATOR).append(SystemUtils.JAVA_SPECIFICATION_VERSION).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("Java Runtime Name").append(COLUMN_SEPARATOR).append(SystemUtils.JAVA_RUNTIME_NAME).append(ROW_SEPARATOR);
            theBuilder.append("P").append(COLUMN_SEPARATOR).append("Java Runtime Version").append(COLUMN_SEPARATOR).append(SystemUtils.JAVA_RUNTIME_VERSION).append(ROW_SEPARATOR);
            // Usecases
            for (Map.Entry<Usecase, UsageData> theEntry : executedUsecase.entrySet()) {
                theBuilder.append("U").append(COLUMN_SEPARATOR).append(theEntry.getKey().toString()).append(COLUMN_SEPARATOR).append(theEntry.getValue().counter).append(ROW_SEPARATOR);
            }
            // Databases
            for (DatabaseConnectionInfo theEntry : connectedDatabases) {
                theBuilder.append("D").append(COLUMN_SEPARATOR).append(theEntry.databaseProductName).append(COLUMN_SEPARATOR).append(theEntry.databaseProductVersion).append(COLUMN_SEPARATOR).append(theEntry.driverName).append(COLUMN_SEPARATOR).append(theEntry.driverVersion).append(ROW_SEPARATOR);
            }

            HttpClient theClient = new HttpClient();
            PostMethod thePost = new PostMethod(POST_URL.toString());
            thePost.setParameter("data", theBuilder.toString());
            int theCode = theClient.executeMethod(thePost);

            LOGGER.info("Got response " + theCode + " from server");
            LOGGER.info("Resonse was [" + thePost.getResponseBodyAsString() + "]");

        } catch (Exception e) {
            LOGGER.error("Error sending usage data", e);
        }
    }
}