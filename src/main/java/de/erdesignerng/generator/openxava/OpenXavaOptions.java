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
package de.erdesignerng.generator.openxava;

import de.erdesignerng.dialect.DataType;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class OpenXavaOptions {

	public static final String[] SUPPORTED_STEREOTYPES = new String[] { "MONEY", "IMAGE", "MEMO", "LABEL",
			"BOLD_LABEL", "TIME", "DATETIME", "IMAGE_GALLERY", "ZEROS_FILLED", "HTML_TEXT", "IMAGE_LABEL", "EMAIL",
			"TELEPHONE", "WEBURL", "IP", "ISBN", "CREDIT_CARD", "EMAIL_LIST" };

	private Map<DataType, OpenXavaTypeMap> typeMapping = new HashMap<>();

	private String srcDirectory;

	private String packageName;

	public OpenXavaOptions() {
	}

	public String createTableName(final String aName) {
		return StringUtils.capitalize(aName.toLowerCase());
	}

	public String createFieldName(final String aName) {
		return aName.toLowerCase();
	}

	public String createPropertyName(final String aName) {
		return StringUtils.capitalize(aName.toLowerCase());
	}

	public String getJavaType(final DataType aDatatype, final boolean aNullable, final boolean aPrimarykey) {
		final OpenXavaTypeMap theType = typeMapping.get(aDatatype);
		String theJavaType = "String";
		if (theType != null) {
			theJavaType = theType.getJavaType();
		}
		if (!aNullable && !aPrimarykey) {
			if ("Integer".equals(theJavaType)) {
				theJavaType = "int";
			}
			if ("Long".equals(theJavaType)) {
				theJavaType = "long";
			}
			if ("Short".equals(theJavaType)) {
				theJavaType = "short";
			}
			if ("Double".equals(theJavaType)) {
				theJavaType = "double";
			}
			if ("Boolean".equals(theJavaType)) {
				theJavaType = "boolean";
			}
		}
		return theJavaType;
	}

	public Map<DataType, OpenXavaTypeMap> getTypeMapping() {
		return typeMapping;
	}

	public void setTypeMapping(final Map<DataType, OpenXavaTypeMap> typeMapping) {
		this.typeMapping = typeMapping;
	}

	/**
	 * @return the srcDirectory
	 */
	public String getSrcDirectory() {
		return srcDirectory;
	}

	/**
	 * @param srcDirectory
	 *			the srcDirectory to set
	 */
	public void setSrcDirectory(final String srcDirectory) {
		this.srcDirectory = srcDirectory;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @param packageName
	 *			the packageName to set
	 */
	public void setPackageName(final String packageName) {
		this.packageName = packageName;
	}
}
