package de.mogwai.erdesignerng.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Properties.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 12:42:47 $
 */
public class ModelProperties {

	private Map<String, String> properties = new HashMap<String, String>();

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> aProperties) {
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
