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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.connector.APPContentManager;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.sourcesense.confluence.cmis.DocinfoMacro;
import com.sourcesense.confluence.cmis.DoclinkMacro;
import com.sourcesense.confluence.cmis.EmbedMacro;
import com.sourcesense.confluence.cmis.SearchMacro;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;

public class IntegrationTestCmisPlugin extends TestCase {
    private static String USERNAME = "admin";
    private static String PASSWORD = "admin";
    private static String CMIS_REPOSITORY_URL = "service/api/cmis";
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

    public void initTest(String url, String username, String password) {
        CMIS_REPOSITORY_URL = url;
        USERNAME = username;
        PASSWORD = password;
    }

    public void testSearch() throws Exception {
        initConfiguration();
        SearchMacro macro = new SearchMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", CMIS_REPOSITORY_URL);
        params.put("u", USERNAME);
        params.put("p", USERNAME);
        String result = macro.execute(params, "SELECT * FROM document", null);

        System.out.println(result);
    }

    public void testDocLink() throws Exception {
        initConfiguration();
        DoclinkMacro macro = new DoclinkMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", CMIS_REPOSITORY_URL);
        params.put("u", USERNAME);
        params.put("p", USERNAME);
        params.put("id", getDocumentId(params.get("s"), params.get("u"), params.get("p")));
        String result = macro.execute(params, null, null);
        String id = getNumericId(params.get("id"));

        assertTrue(result.contains(id));
        System.out.println(result);
    }

    public void testDocInfo() throws Exception {
        initConfiguration();
        DocinfoMacro macro = new DocinfoMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", CMIS_REPOSITORY_URL);
        params.put("u", USERNAME);
        params.put("p", PASSWORD);
        params.put("id", getDocumentId(params.get("s"), params.get("u"), params.get("p")));
        String result = macro.execute(params, null, null);

        System.out.println(result);
    }

    public void testEmbedMacro() throws Exception {
        initConfiguration();
        EmbedMacro macro = new EmbedMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", CMIS_REPOSITORY_URL);
        params.put("u", USERNAME);
        params.put("p", PASSWORD);
        params.put("id", getDocumentId(params.get("s"), params.get("u"), params.get("p")));
        String result = macro.execute(params, null, null);

        System.out.println(result);
    }

    private void initConfiguration() {
        String realm = "http://192.168.1.2:8080/alfresco/";
        Map<String, List<String>> credsMap = new HashMap<String, List<String>>();
        List<String> creds = new LinkedList<String>();
        creds.add("admin");
        creds.add("admin");
        credsMap.put(realm, creds);

        bandanaManager.setValue(null, ConfigureCMISPluginAction.CREDENTIALS_KEY, credsMap);
    }

    private String getNumericId(String id) {
        int startIndex = id.lastIndexOf("/") + 1;
        int endIndex = id.length();
        return id.substring(startIndex, endIndex);
    }

    private String getDocumentId(String serverUrl, String username, String password) {
        ContentManager cm = new APPContentManager(serverUrl);
        String cmisQuery = "SELECT * FROM document";
        cm.login(username, password);
        Repository repository = cm.getDefaultRepository();
        SPI spi = repository.getConnection(null).getSPI();
        Collection<ObjectEntry> res = spi.query(cmisQuery, false, false, false, 1, 0, new boolean[1]);
        for (ObjectEntry entry : res) {
            return entry.getId();
        }
        return null;
    }
}