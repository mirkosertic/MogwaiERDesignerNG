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

    public boolean getBooleanProperty(String aName) {
        if (!properties.containsKey(aName)) {
            return false;
        }

        return Boolean.parseBoolean(properties.get(aName));
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
}