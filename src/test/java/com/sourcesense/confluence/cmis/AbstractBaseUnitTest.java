package com.sourcesense.confluence.cmis;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.utils.CMISVelocityUtils;
import com.sourcesense.confluence.cmis.utils.ConfluenceCMISRepository;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import junit.framework.TestCase;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.StringWriter;
import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBaseUnitTest extends TestCase
{
    protected static final String TEST_REPOSITORY_NAME = "test";
    protected static final String TEST_DOCUMENT_ID = "aCmisDocument";

    protected VelocityEngine ve;
    protected VelocityContext vc;
    protected BandanaManager bandanaManager;
    protected ConfluenceCMISRepository confluenceCMISRepository;

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
        vc.put("cmisUtils", new CMISVelocityUtils());
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

        // CMIS
        ObjectId documentObjectId = mock (ObjectId.class);
        when (documentObjectId.getId()).thenReturn (TEST_DOCUMENT_ID);

        Document documentObject = createMockedCmisObject(new String[][]{
                {PropertyIds.NAME, "Name", "A document name.txt"},
                {PropertyIds.CONTENT_STREAM_LENGTH, "Content Stream Length", "210"},
                {PropertyIds.BASE_TYPE_ID, "Base Type Id", "cmis:document"},
                {PropertyIds.OBJECT_TYPE_ID, "Object Type Id", "cmis:document"},
                {PropertyIds.OBJECT_ID, "Object Type Id", TEST_DOCUMENT_ID}}, Document.class);
        when (documentObject.getId()).thenReturn(TEST_DOCUMENT_ID);

        Session session = mock (Session.class);
        when (session.createObjectId(TEST_DOCUMENT_ID)).thenReturn(documentObjectId);
        when (session.getObject(argThat (new IsDocumentObjectId()))).thenReturn(documentObject);
        
        confluenceCMISRepository = mock (ConfluenceCMISRepository.class);
        when(confluenceCMISRepository.getSession()).thenReturn(session);
    }

    /**
     * Create a stubbed CMIS macro that allows testing without starting Confluence
     * @param clazz The concrete Macro implementation to mock
     * @return The stubbed CMIS macro
     * @throws MacroException If the concrete macro logic fails
     */
    protected <T extends BaseCMISMacro> T createCMISMockMacro (Class<T> clazz) throws MacroException
    {
        T mockMacro = mock (clazz);

        // VelocityUtils would brake with null pointers, override that
        when(mockMacro.render(anyString(), (RenderContext)anyObject())).thenAnswer(new Answer()
        {
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] args = invocation.getArguments();

                String template = (String)args[0];
                RenderContext context = (RenderContext)args[1];

                for (Map.Entry<Object, Object> entry : context.getParams().entrySet())
                {
                    String key = (String)entry.getKey();
                    vc.put (key, entry.getValue());
                }

                return renderTemplate(template);
            }
        });

        // avoid calls to Utils, as we cannot mock the private methods it uses
        when(mockMacro.fetchDocumentLink((ConfluenceCMISRepository)anyObject(), (Session)anyObject(), anyString(), anyBoolean())).thenReturn("http://www.sourcesense.com");

        // let the real business logic be executed, it's usually under test
        when(mockMacro.executeImpl(anyMap(), anyString(), (RenderContext)anyObject(), (ConfluenceCMISRepository)anyObject())).thenCallRealMethod();
        when(mockMacro.getTemplate()).thenCallRealMethod();

        return mockMacro;
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
     * @param clazz Class object in the CmisObject hierarchy that has to be created 
     * @return The mocked CmisObject
     */
    protected <T extends CmisObject> T createMockedCmisObject(String[][] properties, Class<T> clazz)
    {
        T object = mock(clazz);
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

    protected String renderTemplate(String template) throws Exception
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

    class IsDocumentObjectId extends ArgumentMatcher<ObjectId>
    {
        public boolean matches(Object objectId)
        {
            return TEST_DOCUMENT_ID.equals (((ObjectId) objectId).getId());
        }
    }
}
