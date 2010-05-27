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
import com.sourcesense.confluence.cmis.exception.NoRepositoryException;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.log4j.Logger;

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

            RepositoryStorage repositoryStorage = RepositoryStorage.getInstance(bandanaManager);

            Repository repository = getRepositoryFromParams(params, repositoryStorage);
            Session session = repository.createSession();

            return executeImpl (params, body, renderContext, session);

        }
        catch (Exception e)
        {
            logger.error ("Cannot open a session with the CMIS repository", e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }

        return "";
    }

    /**
     * Retrieves a Repository descriptor depending by the macro parameters:
     * - if the repository id is provided ("n") than the repository details are taken from the plugin configuration
     * - otherwise, the server URL ("s"), username ("u") and password ("p") macro parameters are used
     * @param params Parameter map holding the macro parameters
     * @param repositoryStorage Cached repository retriever
     * @return
     * @throws NoRepositoryException
     */
    protected Repository getRepositoryFromParams (Map params, RepositoryStorage repositoryStorage) throws NoRepositoryException
    {
        String repoId = (String) params.get(PARAM_REPOSITORY_ID);

        if (repoId != null && !"".equals (repoId))
        {
            return repositoryStorage.getRepository(repoId);
        }

        String serverUrl = (String) params.get("s");
        String username = (String) params.get("u");
        String password = (String) params.get("p");

        return repositoryStorage.getRepository(serverUrl, username, password);
    }

    protected abstract String executeImpl (Map params, String body, RenderContext renderContext, Session session);

}
