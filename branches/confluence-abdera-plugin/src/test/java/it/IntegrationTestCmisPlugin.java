/*
 * Copyright 2009 Sourcesense <http://www.sourcesense.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import junit.framework.TestCase;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.connector.APPContentManager;
import org.apache.chemistry.atompub.server.servlet.CMISServlet;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.chemistry.impl.simple.SimpleRepository;
import org.apache.chemistry.impl.simple.SimpleType;
import org.apache.velocity.VelocityContext;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.sourcesense.confluence.cmis.DocinfoMacro;
import com.sourcesense.confluence.cmis.DoclinkMacro;
import com.sourcesense.confluence.cmis.EmbedMacro;
import com.sourcesense.confluence.cmis.NavigationMacro;
import com.sourcesense.confluence.cmis.SearchMacro;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import com.sourcesense.confluence.servlets.CMISNavigationServlet;

public class IntegrationTestCmisPlugin extends TestCase {
    private static String USERNAME = "admin";
    private static String PASSWORD = "admin";
    private static String CMIS_REPOSITORY_URL = "http://127.0.0.1:8285/cmis/repository";
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8285;
    public static final String SERVLET_PATH = "/cmis";
    public static final String CMIS_SERVICE = "/repository";
    public Server server;

    BandanaManager bandanaManager = new BandanaManager() {

        Map<String, Object> map = new HashMap<String, Object>();

        public void setValue(BandanaContext context, String key, Object value) {
            map.put(key, value);
        }

        public void init() {
        }

        public void importValues(BandanaContext context, String xmlValues) {
        }

        public Object getValue(BandanaContext context, String key, boolean lookUp) {
            return map.get(key);
        }

        public Object getValue(BandanaContext context, String key) {
            return map.get(key);
        }

        public String exportValues(BandanaContext context) {
            return null;
        }
    };

    VelocityContext context = new VelocityContext() {
        Map<String, Object> map = new HashMap<String, Object>();

        @Override
        public Object clone() {
            return new HashMap<String, Object>(map);
        }

        @Override
        public boolean internalContainsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public Object internalGet(String key) {
            return map.get(key);
        }

        @Override
        public Object[] internalGetKeys() {
            return map.keySet().toArray();
        }

        @Override
        public Object internalPut(String key, Object value) {
            return map.put(key, value);
        }

        @Override
        public Object internalRemove(Object key) {
            return map.remove(key);
        }
    };

    public static Repository makeRepo(String rootId) throws IOException {
        PropertyDefinition p1 = new SimplePropertyDefinition("title", "def:title", "Title", "", false, PropertyType.STRING, false, null, false, false,
                                        "(no title)", Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);
        PropertyDefinition p2 = new SimplePropertyDefinition("description", "def:description", "Description", "", false, PropertyType.STRING, false, null,
                                        false, false, "", Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);
        PropertyDefinition p3 = new SimplePropertyDefinition("date", "def:date", "Date", "", false, PropertyType.DATETIME, false, null, false, false, null,
                                        Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);
        SimpleType dt = new SimpleType("doc", "document", "Doc", "My Doc Type", BaseType.DOCUMENT, "", true, true, true, true, true, true,
                                        ContentStreamPresence.ALLOWED, null, null, Arrays.asList(p1, p2, p3));
        SimpleType ft = new SimpleType("fold", "folder", "Fold", "My Folder Type", BaseType.FOLDER, "", true, true, true, true, false, false,
                                        ContentStreamPresence.NOT_ALLOWED, null, null, Arrays.asList(p1, p2));
        SimpleRepository repo = new SimpleRepository("test", Arrays.asList(dt, ft), rootId);
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();

        Folder folder1 = root.newFolder("fold");
        folder1.setName("folder 1");
        folder1.setValue("title", "The folder 1 description");
        folder1.setValue("description", "folder 1 title");
        folder1.save();

        Folder folder2 = folder1.newFolder("fold");
        folder2.setName("folder 2");
        folder2.setValue("title", "The folder 2 description");
        folder2.setValue("description", "folder 2 title");
        folder2.save();

        Document doc1 = folder1.newDocument("doc");
        doc1.setName("doc 1");
        doc1.setValue("title", "doc 1 title");
        ContentStream cs1 = new SimpleContentStream("prova".getBytes("UTF-8"), "text/plain", "doc1.txt", null);
        doc1.setContentStream(cs1);
        doc1.setValue("description", "The doc 1 descr");
        doc1.save();

        Document doc2 = folder2.newDocument("doc");
        doc2.setName("doc 2");
        doc2.setValue("title", "doc 2 title");
        ContentStream cs2 = new SimpleContentStream("prova".getBytes("UTF-8"), "text/plain", "doc2.txt", null);
        doc2.setContentStream(cs2);
        doc2.setValue("description", "The doc 2 descr");
        doc2.save();

        Document doc3 = folder2.newDocument("doc");
        doc3.setName("doc 3");
        ContentStream cs3 = new SimpleContentStream("prova".getBytes("UTF-8"), "text/plain", "doc3.txt", null);
        doc3.setContentStream(cs3);
        doc3.save();

        conn.close();
        return repo;
    }

    public void stopServer() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public void setUp() throws Exception {
        try {
            Repository repository = makeRepo(null);
            server = new Server();
            Connector connector = new SocketConnector();
            connector.setHost(HOST);
            connector.setPort(PORT);
            server.setConnectors(new Connector[] { connector });
            //set CMIS servlte
            Servlet servlet = new CMISServlet(repository);
            ServletHolder cmisServletHolder = new ServletHolder(servlet);
            Context context = new Context(server, SERVLET_PATH, Context.SESSIONS);
            context.addServlet(cmisServletHolder, "/*");
            //Set Navigation servlet
            CMISNavigationServlet navigationServlet = new CMISNavigationServlet();
            ServletHolder navigationServletHolder = new ServletHolder(navigationServlet);
            navigationServlet.setBandanaManager(bandanaManager);
            Context contexts = new Context(server, "/navigation", Context.SESSIONS);
            contexts.addServlet(navigationServletHolder, "/*");
            //set Resources velocity template
            ResourceHandler handler = new ResourceHandler();
            handler.setResourceBase("./src/test/resources");
            server.addHandler(handler);
            defaultConfiguration();

            server.start();
        } catch (Exception e) {
            stopServer();
            throw e;
        }
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        stopServer();
    }

    public void testNavigation() throws Exception {
        NavigationMacro macro = new NavigationMacro();
        macro.setBandanaManager(bandanaManager);
        macro.setContext(context);
        Map<String, String> params = new HashMap<String, String>();
        params.put("servlet", "http://127.0.0.1:8285/navigation/a");
        params.put("n", "ChemistryLocal");
        params.put("u", "admin");
        params.put("p", "admin");
        String result = macro.execute(params, null, null);
        assertTrue(result.contains("folder 1"));
        System.out.println(result);
    }

    public void testSearch() throws Exception {
        SearchMacro macro = new SearchMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", CMIS_REPOSITORY_URL);
        params.put("u", USERNAME);
        params.put("p", USERNAME);
        String result = macro.execute(params, "SELECT * FROM document WHERE Name='doc 1'", null);
        assertTrue(result.contains("doc 1"));
        System.out.println(result);
    }

    public void testDocLink() throws Exception {
        DoclinkMacro macro = new DoclinkMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", CMIS_REPOSITORY_URL);
        params.put("u", USERNAME);
        params.put("p", USERNAME);
        params.put("id", getObjectId(params.get("s"), params.get("u"), params.get("p"), BaseType.DOCUMENT, null));
        String result = macro.execute(params, null, null);
        String id = getNumericId(params.get("id"));

        assertTrue(result.contains(id));
        System.out.println(result);
    }

    public void testDocInfo() throws Exception {
        DocinfoMacro macro = new DocinfoMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", CMIS_REPOSITORY_URL);
        params.put("u", USERNAME);
        params.put("p", PASSWORD);
        params.put("id", getObjectId(params.get("s"), params.get("u"), params.get("p"), BaseType.DOCUMENT, "WHERE Name = 'doc 2'"));
        String result = macro.execute(params, null, null);
        assertTrue(result.contains("doc 2"));
        assertTrue(result.contains("doc2.txt"));
        System.out.println(result);
    }

    public void testEmbedMacro() throws Exception {
        defaultConfiguration();
        EmbedMacro macro = new EmbedMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", CMIS_REPOSITORY_URL);
        params.put("u", USERNAME);
        params.put("p", PASSWORD);
        params.put("id", getObjectId(params.get("s"), params.get("u"), params.get("p"), BaseType.DOCUMENT, "WHERE Name='doc 2'"));
        String result = macro.execute(params, null, null);

        assertTrue(result.contains("prova"));
        System.out.println(result);
    }

    private void defaultConfiguration() {
        Map<String, List<String>> credsMap = new HashMap<String, List<String>>();
        List<String> creds = new LinkedList<String>();
        creds.add(CMIS_REPOSITORY_URL);
        creds.add("admin");
        creds.add("admin");
        credsMap.put("ChemistryLocal", creds);
        List<String> properties = new LinkedList<String>();
        properties.add("Name");
        properties.add("IsLatestVersion");
        bandanaManager.setValue(null, ConfigureCMISPluginAction.CREDENTIALS_KEY, credsMap);
        bandanaManager.setValue(null, ConfigureCMISPluginAction.SEARCH_PROPERTIES_KEY, properties);
    }

    private String getNumericId(String id) {
        int startIndex = id.lastIndexOf("/") + 1;
        int endIndex = id.length();
        return id.substring(startIndex, endIndex);
    }

    private String getObjectId(String serverUrl, String username, String password, BaseType type, String whereClausole) {
        ContentManager cm = new APPContentManager(serverUrl);
        cm.login(username, password);
        Repository repository = cm.getDefaultRepository();
        Type t = repository.getType(type.toString());
        SPI spi = repository.getConnection(null).getSPI();
        String cmisQuery = null;
        if (whereClausole != null) {
            cmisQuery = "SELECT * FROM " + t.getBaseTypeQueryName() + " " + whereClausole;
        } else {
            cmisQuery = "SELECT * FROM " + t.getBaseTypeQueryName();
        }

        Collection<ObjectEntry> res = spi.query(cmisQuery, false, false, false, 1, 0, new boolean[1]);
        for (ObjectEntry entry : res) {
            return entry.getId();
        }
        return null;
    }
}