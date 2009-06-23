package de.erdesignerng.model;

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

    private Map<String, String> properties = new TreeMap<String, String>();

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

    public int getIntProperty(String aName, int aDefault) {
        if (!properties.containsKey(aName)) {
            return aDefault;
        }

        return Integer.parseInt(properties.get(aName));
    }

    public boolean getBooleanProperty(String aName, boolean aDefault) {
        if (!properties.containsKey(aName)) {
            return aDefault;
        }

        return Boolean.parseBoolean(properties.get(aName));
    }

    public void setIntProperty(String aName, int aValue) {
        properties.put(aName, Integer.toString(aValue));
    }

    public void copyFrom(Model aModel) {
        setProperties(aModel.getProperties().getProperties());
    }

}
