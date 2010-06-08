package com.sourcesense.confluence.cmis.utils;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
@SuppressWarnings("unused")
public class VelocityUtils
{
    public static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");

    public static boolean isNull(Object obj)
    {
        return obj == null;
    }

    public static Map<String, Property> getPropertiesMap(CmisObject object)
    {
        Map<String, Property> propMap = new HashMap<String, Property>();

        for (Property<?> prop : object.getProperties())
        {
            propMap.put(prop.getId(), prop);
        }

        return propMap;
    }

    public static String nullSafe(Object object)
    {
        if (object == null || object instanceof String && ((String) object).length() == 0)
        {
            return " ";
        }

        return object.toString();
    }

    public static boolean isContent(CmisObject object)
    {
        Property contentLenght = object.getProperty(PropertyIds.CONTENT_STREAM_LENGTH);
        return contentLenght != null;
    }

    public static String render(Property property)
    {
        if (property == null)
        {
            return nullSafe(property);
        }
        if (PropertyType.DATETIME.equals(property.getDefinition().getPropertyType()))
        {

            Calendar cal = (Calendar) property.getValues().get(0);
            return sdf.format(cal.getTime());
        }
        else
        {
            return property.getValueAsString();
        }
    }

    /**
     * Translates a namespace qualified property name into a nicer string, like the following:
     *  'cmis:name' -&gt; 'Name'
     *  'xyz:myCustomType' -&gt; 'My Custom Type'
     * @param propName The namespace qualified property name
     * @return The user readable property name
     */
    public static String renderPropertyName(String propName)
    {
        int pos = propName.lastIndexOf(":");
        String result = propName.substring(pos + 1);
        result = result.replaceAll("([A-Z])", " $1");
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result; 
    }
}
