package com.sourcesense.confluence.cmis.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class CMISVelocityUtilsTestCase
{
    @Test
    public void testRenderPropertyName ()
    {
        testRenderer("cmis:name", "Name");
        testRenderer("cmis:versionLabel", "Version Label");
    }

    void testRenderer(String propName, String expected)
    {
        String result = CMISVelocityUtils.renderPropertyName(propName);

        assertEquals(expected, result);
    }
}
