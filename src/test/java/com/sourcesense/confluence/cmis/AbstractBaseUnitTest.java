package com.sourcesense.confluence.cmis;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.sourcesense.confluence.cmis.utils.VelocityNullChecker;
import junit.framework.TestCase;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.*;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public abstract class AbstractBaseUnitTest extends TestCase
{
    protected VelocityEngine ve;
    protected VelocityContext vc;
    protected BandanaManager bandanaManager;

    String cmisRealm = "http://cmis.alfresco.com:80/service/cmis";
    String cmisUser = "admin";
    String cmisPwd = "admin";

    public void setUp() throws Exception
    {
        super.setUp();

        // log4j:
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));

        // Velocity:
        Properties p = new Properties();
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.setProperty("resource.loader", "class");
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
        p.setProperty("runtime.log.logsystem.log4j.category", "velocity");

        vc = new VelocityContext();
        vc.put("check", new VelocityNullChecker());
        ve = new VelocityEngine();
        ve.init(p);

        // Confluence
        Map<String, List<String>> repoConfigs = new WeakHashMap<String, List<String>>();
        List<String> repoConfig = new ArrayList<String>();

        repoConfig.add(cmisRealm);
        repoConfig.add(cmisUser);
        repoConfig.add(cmisPwd);
        //No need to specify a RepositoryID
        repoConfig.add(null);
        repoConfigs.put("test", repoConfig);

        bandanaManager = mock(BandanaManager.class);
        when(bandanaManager.getValue((BandanaContext) anyObject(), anyString())).thenReturn(repoConfigs);
    }

    protected Property<String> createMockedProperty (String displayName, String value)
    {
        Property<String> property = mock(Property.class);
        when(property.getDisplayName()).thenReturn(displayName);
        when(property.getValueAsString()).thenReturn(value);
        return property;
    }

    protected String render (String template) throws Exception
    {
        Template t = ve.getTemplate(template);
        StringWriter sw = new StringWriter();
        t.merge(vc, sw);

        return sw.getBuffer().toString();
    }
}
