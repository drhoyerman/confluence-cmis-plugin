package com.sourcesense.confluence.cmis;

import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.velocity.Template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        Template t = ve.getTemplate("templates/cmis/docinfo.vm");
        StringWriter sw = new StringWriter();

        vc.put("documentProperties", documentProperties);
        vc.put("documentLink", "http://www.sourcesense.com");
        t.merge(vc, sw);

        String renderedView = sw.getBuffer().toString();

        assertNotNull(renderedView);
        assertTrue(renderedView.length() > 0);

        String expectedResult = "*Details of [A nice document.txt|http://www.sourcesense.com]*\n" +
                "||Property||Value||\n" +
                "|Name|A nice document.txt|\n" +
                "|nullProperty| |\n" +
                "|displayName|value|\n";

        assertEquals(expectedResult, renderedView);
    }

    private Property<String> createMockedProperty (String displayName, String value)
    {
        Property<String> property = mock(Property.class);
        when(property.getDisplayName()).thenReturn(displayName);
        when(property.getValueAsString()).thenReturn(value);
        return property;
    }
}
