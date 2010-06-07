package com.sourcesense.confluence.cmis;

import junit.framework.TestCase;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class TestDocInfoMacro extends TestCase
{
    protected VelocityEngine ve;
    protected VelocityContext vc;

    @Override
    public void setUp () throws Exception
    {
        super.setUp();

        // log4j:
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));

        // Velocity:
        Properties p = new Properties();
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.setProperty("resource.loader", "class");
        p.setProperty ("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
        p.setProperty ("runtime.log.logsystem.log4j.category", "velocity");

        vc = new VelocityContext();
        ve = new VelocityEngine();
        ve.init (p);
    }

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
