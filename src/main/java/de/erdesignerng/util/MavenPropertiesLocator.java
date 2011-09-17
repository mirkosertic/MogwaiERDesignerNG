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
package de.erdesignerng.util;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Utility class to deal with maven.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public final class MavenPropertiesLocator {

    public static final String CANNOT_IDENTIFY_VERSION = "Cannot identify version";
    private static String VERSION;

    private MavenPropertiesLocator() {

    }

    /**
     * Get the pom properties for a defined artifact.
     *
     * @param aGroupId    the groupid
     * @param aArtifactId the artifactid
     * @return the properties
     * @throws IOException will be thrown in case of an error
     */
    public static Properties locatePropertiesFor(String aGroupId, String aArtifactId) throws IOException {

        URL theResource = MavenPropertiesLocator.class.getClassLoader().getResource(
                "META-INF/maven/" + aGroupId + "/" + aArtifactId + "/pom.properties");
        Properties theProperties = new Properties();
        theProperties.load(theResource.openStream());

        return theProperties;
    }

    /**
     * Get the version info for a defined artifact.
     *
     * @param aGroupId    the group id
     * @param aArtifactId the artifactid
     * @return the version info
     * @throws IOException will be thrown in case of an error
     */
    public static String getVersionFor(String aGroupId, String aArtifactId) throws IOException {
        Properties theProperties = locatePropertiesFor(aGroupId, aArtifactId);
        return theProperties.getProperty("version");
    }

    /**
     * Get the version info of ERDesignerNG.
     *
     * @return the version info
     */
    public static synchronized String getERDesignerVersionInfo() {
        if (VERSION == null) {
            String theVersion = CANNOT_IDENTIFY_VERSION;
            try {
                theVersion = getVersionFor("net.sourceforge.mogwai", "mogwai-erdesignerng");
            } catch (Exception e) {
                // Nothing shall happen here
            }
            VERSION = theVersion;
        }
        return VERSION;
    }
}