package com.sourcesense.confluence.cmis;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.utils.ConfluenceCMISRepository;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import com.sourcesense.confluence.cmis.utils.VelocityUtils;
import junit.framework.TestCase;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AbstractAtomPubService;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.ObjectServiceImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.Constants;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
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
    protected static final String TEST_REPOSITORY_NAME = "test";

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
        vc.put("cmisUtils", new VelocityUtils());
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
        repoConfigs.put(TEST_REPOSITORY_NAME, repoConfig);

        bandanaManager = mock(BandanaManager.class);
        when(bandanaManager.getValue((BandanaContext) anyObject(), anyString())).thenReturn(repoConfigs);
    }

    @SuppressWarnings("unchecked")
    protected Property<?> createMockedProperty(String id, String displayName, String value)
    {
        Property<String> property = mock(Property.class);

        when(property.getId()).thenReturn(id);
        when(property.getDisplayName()).thenReturn(displayName);
        when(property.getValueAsString()).thenReturn(value);

        PropertyDefinition def = mock(PropertyDefinition.class);
        when(def.getPropertyType()).thenReturn(PropertyType.STRING);
        when(property.getDefinition()).thenReturn(def);
        
        return property;
    }

    /**
     * Create a mock CmisObject using Mockito that will contain the provided properties
     * @param properties Array of arrays in the form of {{propertyId, displayName, propertyValue}}
     * @return The mocked CmisObject
     */
    protected CmisObject createMockedCmisObject(String[][] properties)
    {
        CmisObject object = mock(CmisObject.class);
        List<Property<?>> documentProperties = new ArrayList<Property<?>>();
        for (String[] mockProp : properties)
        {
            Property prop = createMockedProperty(mockProp[0], mockProp[1], mockProp[2]);
            documentProperties.add(prop);
            when(object.getProperty(mockProp[0])).thenReturn(prop);

            if (PropertyIds.NAME.equals(mockProp[0]))
            {
                when(object.getName()).thenReturn(mockProp[2]);
            }
        }
        when(object.getProperties()).thenReturn(documentProperties);

        return object;
    }

    protected String render(String template) throws Exception
    {
        Template t = ve.getTemplate(template);
        StringWriter sw = new StringWriter();
        t.merge(vc, sw);

        return sw.getBuffer().toString();
    }

    protected Session getSession(String repositoryId)
    {
        RepositoryStorage repoStorage = RepositoryStorage.getInstance(bandanaManager);

        return repoStorage.getRepository(repositoryId).getSession();
    }
}
