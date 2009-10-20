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

import java.util.Map;

import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.APPConnection;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.exception.NoRepositoryException;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;

public abstract class BaseCMISMacro extends BaseMacro {
    // This constant must be in according with servlet url-pattern in atlassian-plugin.xml
    protected BandanaManager bandanaManager;
    protected String serverName;

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

            /*
            try {
                StaxReader reader = StaxReader.newReader(System.in);
            } catch (XMLStreamException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
             */
            RepositoryStorage repositoryStorage = RepositoryStorage.getInstance(bandanaManager);
            Repository repository = null;
            String serverUrl;
            String username = null;
            String password = null;
            serverName = (String) params.get("n");
            if (serverName == null) {
                serverUrl = (String) params.get("s");
                username = (String) params.get("u");
                password = (String) params.get("p");
                repository = repositoryStorage.getRepository(serverUrl, username, password);
            } else {
                try {
                    repository = repositoryStorage.getRepository(serverName);
                } catch (NoRepositoryException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
            return doExecute(params, body, renderContext, repository);

        } finally {
            // Restore original classloader
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
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

    protected abstract String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException;
}
