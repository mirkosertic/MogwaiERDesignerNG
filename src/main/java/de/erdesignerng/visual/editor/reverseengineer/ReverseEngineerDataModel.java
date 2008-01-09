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
package de.erdesignerng.visual.editor.reverseengineer;

import java.util.ArrayList;
import java.util.List;

public class ReverseEngineerDataModel {

	private NameValuePair tableGenerator;

	private NameValuePair domainGenerator;

	private NameValuePair defaultValueGenerator;

	private List tableOptions = new ArrayList();

	private List domainOptions = new ArrayList();

	private List defaultValueOptions = new ArrayList();

	/**
	 * @return the defaultValueGenerator
	 */
	public NameValuePair getDefaultValueGenerator() {
		return defaultValueGenerator;
	}

	/**
	 * @param defaultValueGenerator
	 *            the defaultValueGenerator to set
	 */
	public void setDefaultValueGenerator(NameValuePair defaultValueGenerator) {
		this.defaultValueGenerator = defaultValueGenerator;
	}

	/**
	 * @return the domainGenerator
	 */
	public NameValuePair getDomainGenerator() {
		return domainGenerator;
	}

	/**
	 * @param domainGenerator
	 *            the domainGenerator to set
	 */
	public void setDomainGenerator(NameValuePair domainGenerator) {
		this.domainGenerator = domainGenerator;
	}

	/**
	 * @return the tableGenerator
	 */
	public NameValuePair getTableGenerator() {
		return tableGenerator;
	}

	/**
	 * @param tableGenerator
	 *            the tableGenerator to set
	 */
	public void setTableGenerator(NameValuePair tableGenerator) {
		this.tableGenerator = tableGenerator;
	}

	/**
	 * @return the defaultValueOptions
	 */
	public List getDefaultValueOptions() {
		return defaultValueOptions;
	}

	/**
	 * @param defaultValueOptions
	 *            the defaultValueOptions to set
	 */
	public void setDefaultValueOptions(List defaultValueOptions) {
		this.defaultValueOptions = defaultValueOptions;
	}

	/**
	 * @return the domainOptions
	 */
	public List getDomainOptions() {
		return domainOptions;
	}

	/**
	 * @param domainOptions
	 *            the domainOptions to set
	 */
	public void setDomainOptions(List domainOptions) {
		this.domainOptions = domainOptions;
	}

	/**
	 * @return the tableOptions
	 */
	public List getTableOptions() {
		return tableOptions;
	}

	/**
	 * @param tableOptions
	 *            the tableOptions to set
	 */
	public void setTableOptions(List tableOptions) {
		this.tableOptions = tableOptions;
	}

}
