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

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

// TODO: put back in place the subclass delegation of behavior
public abstract class BaseCMISMacro extends BaseMacro
{
    private static final Logger logger = Logger.getLogger("com.sourcesense.confluence.cmis");

    public static final String PARAM_REPOSITORY_ID = "n";
    public static final String PARAM_SERVER_URL = "s";
    public static final String PARAM_USERNAME = "u";
    public static final String PARAM_PASSWORD = "p";

    protected BandanaManager bandanaManager;
    protected SettingsManager settingsManager;
    protected String serverName;

    public void setBandanaManager(BandanaManager bandanaManager)
    {
        this.bandanaManager = bandanaManager;
    }

    public void setSettingsManager(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
    }

    @SuppressWarnings("unchecked")
    public String execute(Map params, String body, RenderContext renderContext) throws MacroException
    {
        // Work around to let OpenCMIS using the bundled version of Jaxb
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            String repoId = (String) params.get(PARAM_REPOSITORY_ID);

            RepositoryStorage repositoryStorage = RepositoryStorage.getInstance(bandanaManager);

            // TODO: fetch the repo with different behaviors depending on the set of input params
            Repository repository = repositoryStorage.getRepository(repoId);
            Session session = repository.createSession();

            return executeImpl (params, body, renderContext, session);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }

        return "";


/*
        // Work around Abdera trying to be smart with class loading
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            Logger logger = Logger.getLogger("com.sourcesense.confluence.cmis.BaseCMISMacro");
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
                    System.out.println(e.getMessage());
                    return e.getMessage();
                }
            }
            return doExecute(params, body, renderContext, repository);
        } finally {
            // Restore original classloader
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
*/


    }

    protected abstract String executeImpl (Map params, String body, RenderContext renderContext, Session session);

}
