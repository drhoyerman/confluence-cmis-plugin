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
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
  protected static final String TEST_SERVER_NAME = "test";
  protected static final String TEST_REALM = "http://cmis.alfresco.com:80/service/cmis";
  protected static final String TEST_USERNAME = "admin";
  protected static final String TEST_PASSWORD = "admin";

    protected static final String TEST_DOCUMENT_ID = "aCmisDocument";
    protected static final String TEST_FOLDER_ID = "aCmisFolder";

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

        // CMIS Repository

        Map<String, ConfluenceCMISRepository> repoConfigs = new WeakHashMap<String, ConfluenceCMISRepository>();
        ConfluenceCMISRepository repoConfig = new ConfluenceCMISRepository(TEST_SERVER_NAME,
            TEST_REALM,
            TEST_USERNAME,
            TEST_PASSWORD,
            null);

        repoConfigs.put(TEST_SERVER_NAME, repoConfig);

        bandanaManager = mock(BandanaManager.class);
        when(bandanaManager.getValue((BandanaContext) anyObject(), anyString())).thenReturn(repoConfigs);

        // CMIS
        ObjectId documentObjectId = mock (ObjectId.class);
        when (documentObjectId.getId()).thenReturn (TEST_DOCUMENT_ID);

        Document documentObject = getTestDocument();

        List <CmisObject> documentList = new ArrayList<CmisObject> ();
        documentList.add(documentObject);

        ObjectId folderObjectId = mock (ObjectId.class);
        when (folderObjectId.getId()).thenReturn (TEST_FOLDER_ID);

        ItemIterable<CmisObject> children = mock (ItemIterable.class);
        when (children.getTotalNumItems()).thenReturn(1l);
        when (children.getHasMoreItems()).thenReturn(true).thenReturn(false);
        when (children.iterator()).thenReturn (documentList.iterator());
        
        Folder folderObject = createMockedCmisObject(new String[][]{
                {PropertyIds.NAME, "Name", "A folder"},
                {PropertyIds.BASE_TYPE_ID, "Base Type Id", "cmis:folder"},
                {PropertyIds.OBJECT_TYPE_ID, "Object Type Id", "cmis:folder"},
                {PropertyIds.OBJECT_ID, "Object Type Id", TEST_FOLDER_ID}}, Folder.class);
        when (folderObject.getId()).thenReturn(TEST_FOLDER_ID);
        when (folderObject.getChildren()).thenReturn(children);

        Session session = mock (Session.class);

        when (session.createObjectId(TEST_DOCUMENT_ID)).thenReturn(documentObjectId);
        when (session.createObjectId(TEST_FOLDER_ID)).thenReturn(folderObjectId);
        when (session.getObject(documentObjectId)).thenReturn(documentObject);
        when (session.getObject(folderObjectId)).thenReturn(folderObject);
        
        confluenceCMISRepository = mock (ConfluenceCMISRepository.class);
        when(confluenceCMISRepository.getSession()).thenReturn(session);
    }

    protected Document getTestDocument()
    {
        ObjectType documentObjectType = mock (ObjectType.class);
        when (documentObjectType.getId()).thenReturn(BaseTypeId.CMIS_DOCUMENT.value());

        Document documentObject = createMockedCmisObject(new Object[][]{
                {PropertyIds.NAME, "Name", "A document name.txt"},
                {PropertyIds.CONTENT_STREAM_LENGTH, "Content Stream Length", "210"},
                {PropertyIds.BASE_TYPE_ID, "Base Type Id", "cmis:document"},
                {PropertyIds.OBJECT_TYPE_ID, "Object Type Id", "cmis:document"},
                {PropertyIds.LAST_MODIFICATION_DATE, "Last Modification Date", new Date(0)},
                {PropertyIds.OBJECT_TYPE_ID, "Object Type Id", "cmis:document"},
                {PropertyIds.CONTENT_STREAM_MIME_TYPE, "Content Stream Mime Type", "text/plain"},
                {PropertyIds.OBJECT_ID, "Object Type Id", TEST_DOCUMENT_ID}}, Document.class);
        when (documentObject.getId()).thenReturn(TEST_DOCUMENT_ID);
        when (documentObject.getBaseType()).thenReturn(documentObjectType);
        
        return documentObject;
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

        when(mockMacro.fetchDocumentLink((ConfluenceCMISRepository)anyObject(), (Session)anyObject(), anyString(), anyBoolean())).thenReturn("http://www.sourcesense.com");

        // let the real business logic be executed, it's usually under test
        when(mockMacro.executeImpl(anyMap(), anyString(), (RenderContext)anyObject(), (ConfluenceCMISRepository)anyObject())).thenCallRealMethod();
        when(mockMacro.getTemplate()).thenCallRealMethod();

        return mockMacro;
    }

    @SuppressWarnings("unchecked")
    protected Property<?> createMockedProperty(Object id, Object displayName, Object value)
    {
        Property<String> property = mock(Property.class);

        when(property.getId()).thenReturn(id.toString()).toString();
        when(property.getDisplayName()).thenReturn(displayName.toString());
        when(property.getValueAsString()).thenReturn(value == null ? "" : value.toString());

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
    protected <T extends CmisObject> T createMockedCmisObject(Object[][] properties, Class<T> clazz)
    {
        T object = mock(clazz);
        List<Property<?>> documentProperties = new ArrayList<Property<?>>();
        for (Object[] mockProp : properties)
        {
            Property prop = createMockedProperty(mockProp[0], mockProp[1], mockProp[2]);
            documentProperties.add(prop);
            when(object.getProperty(mockProp[0].toString())).thenReturn(prop);

            if (PropertyIds.NAME.equals(mockProp[0]))
            {
                when(object.getName()).thenReturn(mockProp[2].toString());
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

    protected Session getSession(String serverName)
    {
        RepositoryStorage repoStorage = RepositoryStorage.getInstance(bandanaManager);

        return repoStorage.getRepository(serverName).getSession();
    }
}
