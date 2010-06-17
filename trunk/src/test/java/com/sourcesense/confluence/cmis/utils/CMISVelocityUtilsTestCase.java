package com.sourcesense.confluence.cmis.utils;

import com.sourcesense.confluence.cmis.utils.CMISVelocityUtils;
import junit.framework.TestCase;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class CMISVelocityUtilsTestCase extends TestCase
{
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
