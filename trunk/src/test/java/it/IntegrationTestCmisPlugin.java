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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.renderer.RenderContext;
import com.sourcesense.confluence.cmis.SearchMacro;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;

public class IntegrationTestCmisPlugin extends TestCase
{
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

    public void testSearch() throws Exception
    {
        String realm = "http://192.168.1.8:8080/alfresco";
        Map<String, List<String>> credsMap = new HashMap<String, List<String>>();
        List<String> creds = new LinkedList<String>();
        creds.add("admin");
        creds.add("admin");
        credsMap.put(realm, creds);
        bandanaManager.setValue(null, ConfigureCMISPluginAction.CREDENTIALS_KEY, credsMap);
        
        SearchMacro macro = new SearchMacro();
        macro.setBandanaManager(bandanaManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("s", "http://192.168.1.8:8080/alfresco/service/api/cmis");
        String result = macro.execute(params, "SELECT * FROM DOCUMENT WHERE CONTAINS('alfresco')", null);
        
        System.out.println(result);
    }
}