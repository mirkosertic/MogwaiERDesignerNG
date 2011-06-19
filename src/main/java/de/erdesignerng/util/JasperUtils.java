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

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class to deal with jasper reports.
 */
public final class JasperUtils {

	private JasperUtils() {
	}

	private static void findReports(Map<File, String> aReportMap, File aDirectory) throws JRException {
		Thread.currentThread().setContextClassLoader(JasperUtils.class.getClassLoader());
		for (File theFile : aDirectory.listFiles()) {
			String theName = theFile.getName();
			if (theName.endsWith(".jrxml") && (theName.indexOf("_") < 0)) {
				try {
					JasperDesign theDesign = JRXmlLoader.load(new FileInputStream(theFile));
					String theReportName = theDesign.getName();
					if (!StringUtils.isEmpty(theReportName)) {
						aReportMap.put(theFile, theReportName);
					}
				} catch (FileNotFoundException e) {
					// This cannot happen
				}

			} else {
				if ((theFile.isDirectory()) && (!".".equals(theName)) && (!"..".equals(theName))) {
					findReports(aReportMap, theFile);
				}
			}
		}
	}

	public static Map<File, String> findReportsInDirectory(File aDirectory) throws JRException {
		Map<File, String> theResult = new HashMap<File, String>();
		findReports(theResult, aDirectory);
		return theResult;
	}

	public static JasperPrint runJasperReport(File aModelXMLFile, File aJRXMLFile) throws JRException,
			FileNotFoundException {

		String theFileName = aJRXMLFile.getAbsolutePath();
		int p = theFileName.indexOf(".jrxml");
		String theTemplateName = theFileName.substring(0, p) + ".jasper";

		File theTemplateFile = new File(theTemplateName);

		JasperDesign theDesign = JRXmlLoader.load(new FileInputStream(aJRXMLFile));
		JRQuery theQuery = theDesign.getQuery();
		String theQueryText = null;

		if (theQuery != null) {
			theQueryText = theQuery.getText();
		}
		if (StringUtils.isEmpty(theQueryText)) {
			throw new RuntimeException("Cannot extract query from Jasper template");
		}

		Map<Object, Object> theParams = new HashMap<Object, Object>();
		theParams.put(JRParameter.REPORT_LOCALE, Locale.getDefault());

		String theSubreportDir = theTemplateFile.getParent();
		if (!theSubreportDir.endsWith(File.separator)) {
			theSubreportDir += File.separator;
		}
		theParams.put("SUBREPORT_DIR", theSubreportDir);

		JRXmlDataSource theDataSource = new JRXmlDataSource(aModelXMLFile, theQueryText);
		return JasperFillManager.fillReport(new FileInputStream(theTemplateFile), theParams,
				theDataSource);
	}
}
