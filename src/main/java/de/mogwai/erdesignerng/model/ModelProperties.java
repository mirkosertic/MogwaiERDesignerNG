package de.mogwai.erdesignerng.model;

import java.util.HashMap;

/**
 * Properties.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-27 18:23:36 $
 */
public class ModelProperties {

	private HashMap<String, String> properties = new HashMap<String, String>();

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> aProperties) {
		properties = aProperties;
	}

	public void setProperty(String aName, String aValue) {
		properties.put(aName, aValue);
	}

	public String getProperty(String aName) {
		return properties.get(aName);
	}

	public int getIntProperty(String aName, int aDefault) {
		if (!properties.containsKey(aName)) {
			return aDefault;
		}

		return Integer.parseInt(properties.get(aName));
	}

	public void setIntProperty(String aName, int aValue) {
		properties.put(aName, "" + aValue);
	}

}
