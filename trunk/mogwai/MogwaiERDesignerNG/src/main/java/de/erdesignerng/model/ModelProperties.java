package de.erdesignerng.model;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Properties.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-14 21:10:04 $
 */
public class ModelProperties implements Serializable {

	private Map<String, String> properties = new TreeMap<>();

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> aProperties) {
		properties = aProperties;
	}

	public void setProperty(String aName, String aValue) {
		properties.put(aName, aValue);
	}

	public void setProperty(String aName, boolean aValue) {
		properties.put(aName, Boolean.toString(aValue));
	}

	public String getProperty(String aName) {
		return properties.get(aName);
	}

	public boolean getBooleanProperty(String aName) {
		if (!properties.containsKey(aName)) {
			return false;
		}

		return Boolean.parseBoolean(properties.get(aName));
	}

	public void setBooleanProperty(String aName, boolean aState) {
		properties.put(aName, Boolean.toString(aState));
	}

	public void setPointProperty(String aKey, int x, int y) {
		String theLocation = x + ":" + y;
		setProperty(aKey, theLocation);
	}

	/**
	 * Convert a Point2D to a String.
	 *
	 * @param aPoint
	 * @return string, containing coordinates; separated by colon
	 */
	public static String toString(Point2D aPoint) {
		return "" + (int) aPoint.getX() + ":" + (int) aPoint.getY();

	}

	/**
	 * Convert a String to a Point2D.
	 *
	 * @param aValue
	 * @return Point2D representation of a string containing coordinates
	 */
	public static Point2D toPoint2D(String aValue) {

		if (aValue == null) {
			return null;
		}

		int theP = aValue.indexOf(":");
		int theX = Integer.parseInt(aValue.substring(0, theP));
		int theY = Integer.parseInt(aValue.substring(theP + 1));

		return new Point2D.Double(theX, theY);
	}

	public Point2D getPoint2DProperty(String aKey) {
		return toPoint2D(getProperty(aKey));
	}


	public void copyFrom(Model aModel) {
		setProperties(aModel.getProperties().getProperties());
	}

	/**
	 * Test if the properties were modified in comparison to aother property set.
	 *
	 * @param aOtherProperties the other propertie
	 * @return true if they were modified, else false
	 */
	public boolean isModified(ModelProperties aOtherProperties) {

		if (!properties.keySet().containsAll(aOtherProperties.properties.keySet())) {
			return true;
		}
		if (!aOtherProperties.properties.keySet().containsAll(properties.keySet())) {
			return true;
		}

		for (Map.Entry<String, String> theKey : properties.entrySet()) {
			String theOtherValue = aOtherProperties.getProperty(theKey.getKey());
			if (theKey.getValue() != null) {
				if (!theKey.getValue().equals(theOtherValue)) {
					return true;
				}
			} else {
				if (theOtherValue != null) {
					return true;
				}
			}
		}

		return false;
	}

	public void copyFrom(ModelProperties aProperties) {
		for (Map.Entry<String, String> theEntry : aProperties.properties.entrySet()) {
			properties.put(theEntry.getKey(), theEntry.getValue());
		}
	}
}