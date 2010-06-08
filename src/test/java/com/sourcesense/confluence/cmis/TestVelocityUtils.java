package com.sourcesense.confluence.cmis;

import com.sourcesense.confluence.cmis.utils.VelocityUtils;
import junit.framework.TestCase;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class TestVelocityUtils extends TestCase
{
    public void testRenderPropertyName ()
    {
        testRenderer("cmis:name", "Name");
        testRenderer("cmis:versionLabel", "Version Label");
    }

    void testRenderer(String propName, String expected)
    {
        String result = VelocityUtils.renderPropertyName(propName);

        assertEquals(expected, result);
    }
}
