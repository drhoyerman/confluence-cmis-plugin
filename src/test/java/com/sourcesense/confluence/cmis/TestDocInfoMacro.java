package com.sourcesense.confluence.cmis;

import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.velocity.Template;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
        List<Property<?>> documentProperties = new ArrayList<Property<?>> ();

        Property<String> property = mock(Property.class);
        when(property.getValueAsString()).thenReturn("value");
        when(property.getDisplayName()).thenReturn("displayName");
        
        documentProperties.add(property);

        Template t = ve.getTemplate("templates/cmis/docinfo.vm");
        StringWriter sw = new StringWriter();

        vc.put("documentProperties", documentProperties);
        t.merge(vc, sw);

        String renderedView = sw.getBuffer().toString();

        assertNotNull(renderedView);
        assertTrue(renderedView.length() > 0);

        String expectedResult = "||Property||Value||\n" +
                "|displayName|value|";

        assertEquals(expectedResult, renderedView);
    }
}
