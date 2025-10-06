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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.lang.StringUtils;

/**
 * Utility class to deal with jasper reports.
 */
public final class JasperUtils {

	private JasperUtils() {
	}

	private static void findReports(final Map<File, String> aReportMap, final File aDirectory) {
		Thread.currentThread().setContextClassLoader(JasperUtils.class.getClassLoader());
		if (aDirectory != null && aDirectory.exists() && aDirectory.canRead()) {
			for (final File theFile : aDirectory.listFiles()) {
				final String theName = theFile.getName();
				if (theName.endsWith(".jrxml") && (!theName.contains("_"))) {
					try {
						final JasperDesign theDesign = JRXmlLoader.load(new FileInputStream(theFile));
						final String theReportName = theDesign.getName();
						if (StringUtils.isNotEmpty(theReportName)) {
							aReportMap.put(theFile, theReportName);
						}
					} catch (final FileNotFoundException e) {
						// This cannot happen
					} catch (final JRException e) {
                        // This is not a valid report
                        System.out.println("Invalid report, please migrate: " + theFile.getAbsolutePath());
                    }

				} else {
					if ((theFile.isDirectory()) && (!".".equals(theName)) && (!"..".equals(theName))) {
						findReports(aReportMap, theFile);
					}
				}
			}
		}
	}

	public static Map<File, String> findReportsInDirectory(final File aDirectory) throws JRException {
		final Map<File, String> theResult = new HashMap<>();
		findReports(theResult, aDirectory);
		return theResult;
	}

	public static JasperPrint runJasperReport(final File aModelXMLFile, final File aJRXMLFile) throws JRException,
			FileNotFoundException {

		final String theFileName = aJRXMLFile.getAbsolutePath();
		final int p = theFileName.indexOf(".jrxml");
		final String theTemplateName = theFileName.substring(0, p) + ".jasper";

		final File theTemplateFile = new File(theTemplateName);

		final JasperDesign theDesign = JRXmlLoader.load(new FileInputStream(aJRXMLFile));
		final JRQuery theQuery = theDesign.getQuery();
		String theQueryText = null;

		if (theQuery != null) {
			theQueryText = theQuery.getText();
		}
		if (StringUtils.isEmpty(theQueryText)) {
			throw new RuntimeException("Cannot extract query from Jasper template");
		}

		final Map<String, Object> theParams = new HashMap<>();
		theParams.put(JRParameter.REPORT_LOCALE, Locale.getDefault());

		String theSubreportDir = theTemplateFile.getParent();
		if (!theSubreportDir.endsWith(File.separator)) {
			theSubreportDir += File.separator;
		}
		theParams.put("SUBREPORT_DIR", theSubreportDir);

		final JRXmlDataSource theDataSource = new JRXmlDataSource(aModelXMLFile, theQueryText);
		return JasperFillManager.fillReport(new FileInputStream(theTemplateFile), theParams,
				theDataSource);
	}
}
