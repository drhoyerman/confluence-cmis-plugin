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
package com.sourcesense.confluence.cmis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.atompub.client.APPConnection;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.connector.APPContentManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;

public abstract class BaseCMISMacro extends BaseMacro {

    private BandanaManager bandanaManager;
    
    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }
    
    @SuppressWarnings("unchecked")
    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        
        // Work around Abdera trying to be smart with class loading
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            
            Abdera abdera = new Abdera();
            
            if (abdera.getParser() == null) {
                return "NO PARSER!";
            }
        
            /*
            try {
                StaxReader reader = StaxReader.newReader(System.in);
            } catch (XMLStreamException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
    
            String serverUrl = (String) params.get("s");
            String repositoryUsername = (String) params.get("u");
            String repositoryPassword = (String) params.get("p");
            UsernamePasswordCredentials credentials = getCredentials(serverUrl, repositoryUsername, repositoryPassword);
            ContentManager cm = new APPContentManager(serverUrl);
            cm.login(credentials.getUserName(), credentials.getPassword());
            Repository repository = cm.getDefaultRepository();
            return doExecute(params, body, renderContext, repository);
        
        } finally {
            // Restore original classloader
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }
    
    @SuppressWarnings("unchecked")
    protected UsernamePasswordCredentials getCredentials(String url, String username, String password) {
        if (username != null && password != null) {
            return new UsernamePasswordCredentials(username , password);
        }
        Map<String, List<String>> credsMap = (Map<String, List<String>>) this.bandanaManager.getValue(new ConfluenceBandanaContext(), 
                                        ConfigureCMISPluginAction.CREDENTIALS_KEY);
        if (credsMap == null) {
            return null;
        }
        for (String realm : credsMap.keySet()) {
            if (url.startsWith(realm)) {
                List<String> up = credsMap.get(realm);
                return new UsernamePasswordCredentials(up.get(0), up.get(1));
            }
        }
        return null;
    }

    /**
     * Gets a CMISObject using its ID.
     * We temprarily use this method until Chemistry's 
     * {@link APPConnection#getObject(org.apache.chemistry.ObjectId, org.apache.chemistry.ReturnVersion)} works properly.
     * 
     * @param repository The {@link Repository to query}
     * @param id The object's ID.
     * @return The object with the given ID, if it exists, otherwise null.
     */
    protected CMISObject getEntryViaID(Repository repository, String id) {
        String cmisQuery = "SELECT * FROM DOCUMENT WHERE ObjectId = '" + id + "'";
        Connection conn = repository.getConnection(null);
        SPI spi = conn.getSPI();
        Collection<ObjectEntry> res = spi.query(cmisQuery, false, false, false, false, 1, 0, new boolean[1]);
        for (ObjectEntry entry : res) {
            return conn.getObject(entry);
        }
        return null;
    }

    protected abstract String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException;
}
