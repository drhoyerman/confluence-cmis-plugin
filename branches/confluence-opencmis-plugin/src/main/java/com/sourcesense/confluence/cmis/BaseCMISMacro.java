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

            StringBuilder sb = new StringBuilder("test OpenCMIS integration");

            String repositoryUsername = (String) params.get("u");
            String repositoryPassword = (String) params.get("p");
            String url = (String) params.get("s");
            String id = (String) params.get("id");
            System.out.println("u ->" + repositoryUsername);
            System.out.println("p ->" + repositoryPassword);
            System.out.println("s ->" + url);
            System.out.println("id ->" + id);


            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
            parameters.put(SessionParameter.ATOMPUB_URL, url);
            parameters.put(SessionParameter.REPOSITORY_ID, id);
            parameters.put(SessionParameter.USER, repositoryUsername);
            parameters.put(SessionParameter.PASSWORD, repositoryPassword);

            System.out.println("Initialized login data");

            Session session = SessionFactoryImpl.newInstance().createSession(parameters);

            System.out.println("Initialized session");

// get repository info
            RepositoryInfo repInfo = session.getRepositoryInfo();
            sb.append("Repository name: " + repInfo.getName() + "\n");
            System.out.println("Repository name: " + repInfo.getName() + "\n");
            System.out.println("Repository id: " + repInfo.getId() + "\n");

// get root folder and its path
            Folder rootFolder = session.getRootFolder();
            String path = rootFolder.getPath();
            sb.append("Root folder path: " + path + "\n");
            System.out.println("Root folder path: " + path + "\n");

// list root folder children
            ItemIterable<CmisObject> children = rootFolder.getChildren();
            for (CmisObject object : children)
            {
                sb.append("---------------------------------");
                sb.append("- Id:               " + object.getId() + "\n");
                sb.append("- Name:             " + object.getName() + "\n");
                sb.append("- Base Type:        " + object.getBaseTypeId() + "\n");
                sb.append("- Property 'bla':   " + object.getPropertyValue("bla") + "\n");

                ObjectType type = object.getType();
                sb.append("- Type Id:          " + type.getId() + "\n");
                sb.append("- Type Name:        " + type.getDisplayName() + "\n");
                sb.append("- Type Query Name:  " + type.getQueryName() + "\n");

                AllowableActions actions = object.getAllowableActions();
                sb.append("- canGetProperties: " + actions.getAllowableActions().contains(Action.CAN_GET_PROPERTIES) + "\n");
                sb.append("- canDeleteObject:  " + actions.getAllowableActions().contains(Action.CAN_DELETE_OBJECT) + "\n");
            }
            return sb.toString();
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

    @Deprecated
    private void opencmisStuff()
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameters.put(SessionParameter.ATOMPUB_URL, "http://localhost:8080/opencmis/atom");
        parameters.put(SessionParameter.REPOSITORY_ID, "A1");
        parameters.put(SessionParameter.USER, "test");
        parameters.put(SessionParameter.PASSWORD, "test");

// create the session
        Session session = SessionFactoryImpl.newInstance().createSession(parameters);

// get repository info
        RepositoryInfo repInfo = session.getRepositoryInfo();
        System.out.println("Repository name: " + repInfo.getName());

// get root folder and its path
        Folder rootFolder = session.getRootFolder();
        String path = rootFolder.getPath();
        System.out.println("Root folder path: " + path);

// list root folder children
        ItemIterable<CmisObject> children = rootFolder.getChildren();
        for (CmisObject object : children)
        {
            System.out.println("---------------------------------");
            System.out.println("  Id:               " + object.getId());
            System.out.println("  Name:             " + object.getName());
            System.out.println("  Base Type:        " + object.getBaseTypeId());
            System.out.println("  Property 'bla':   " + object.getPropertyValue("bla"));

            ObjectType type = object.getType();
            System.out.println("  Type Id:          " + type.getId());
            System.out.println("  Type Name:        " + type.getDisplayName());
            System.out.println("  Type Query Name:  " + type.getQueryName());

            AllowableActions actions = object.getAllowableActions();
            System.out.println("  canGetProperties: " + actions.getAllowableActions().contains(Action.CAN_GET_PROPERTIES));
            System.out.println("  canDeleteObject:  " + actions.getAllowableActions().contains(Action.CAN_DELETE_OBJECT));
        }

// get an object
        ObjectId objectId = session.createObjectId("100");
        CmisObject object = session.getObject(objectId);

        if (object instanceof Folder)
        {
            Folder folder = (Folder) object;
            System.out.println("Is root folder: " + folder.isRootFolder());
        }

        if (object instanceof Document)
        {
            Document document = (Document) object;
            ContentStream content = document.getContentStream();
            System.out.println("Document MIME type: " + content.getMimeType());
        }
    }

}
