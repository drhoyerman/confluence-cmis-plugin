package com.sourcesense.confluence.cmis;

import org.apache.chemistry.opencmis.client.api.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class TestDocInfoMacro extends AbstractBaseUnitTest
{
    @SuppressWarnings("unchecked")
    public void testRenderDocumentInfo() throws Exception
    {
        Map<String, String> documentProperties = new HashMap<String, String>();

        Property<String> prop = createMockedProperty ("displayName", "value");
        documentProperties.put(prop.getDisplayName(), prop.getValueAsString());

        prop = createMockedProperty ("Name", "A nice document.txt");
        documentProperties.put(prop.getDisplayName(), prop.getValueAsString());

        prop = createMockedProperty ("nullProperty", null);
        documentProperties.put(prop.getDisplayName(), prop.getValueAsString());

        vc.put("documentProperties", documentProperties);
        vc.put("documentLink", "http://www.sourcesense.com");

        String renderedView = render ("templates/cmis/docinfo.vm");

        assertNotNull(renderedView);
        assertTrue(renderedView.length() > 0);

        String expectedResult = "*Details of [A nice document.txt|http://www.sourcesense.com]*\n" +
                "||Property||Value||\n" +
                "|Name|A nice document.txt|\n" +
                "|nullProperty| |\n" +
                "|displayName|value|\n";

        assertEquals(expectedResult, renderedView);
    }
}
