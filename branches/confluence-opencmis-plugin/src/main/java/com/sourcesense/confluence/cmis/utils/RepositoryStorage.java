package com.sourcesense.confluence.cmis.utils;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import com.sourcesense.confluence.cmis.exception.NoRepositoryException;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import java.util.*;

public class RepositoryStorage
{

    private ConfluenceBandanaContext context = new ConfluenceBandanaContext();
    private Map<String, Repository> repositories;
    private BandanaManager bandanaManager;
    private static RepositoryStorage repositoryStorage;

    public static RepositoryStorage getInstance(BandanaManager bandanaManager)
    {
        if (repositoryStorage == null)
        {
            repositoryStorage = new RepositoryStorage();
            repositoryStorage.setBandanaManager(bandanaManager);
            // TODO: what if the BandanaManager differs?
        }

        return repositoryStorage;
    }


    public static RepositoryStorage resetAndGetInstance(Map<String, Repository> cache, BandanaManager bandanaManager)
    {
        repositoryStorage = new RepositoryStorage(cache);
        return getInstance(bandanaManager);
    }

    public RepositoryStorage()
    {
        this.repositories = new WeakHashMap<String, Repository>();
    }

    public RepositoryStorage(Map<String, Repository> cache)
    {
        this.repositories = cache;
    }


    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getRepositoriesMap()
    {
        return ((Map<String, List<String>>) this.bandanaManager.getValue(context, ConfigureCMISPluginAction.CREDENTIALS_KEY));
    }


    public Repository getRepository(String repoName) throws NoRepositoryException
    {
        if (!repositories.containsKey(repoName))
        {
            List<String> repositoryConfig = getRepositoriesMap().get(repoName);
            if (repositoryConfig != null)
            {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
                parameters.put(SessionParameter.ATOMPUB_URL, repositoryConfig.get(ConfigureCMISPluginAction.PARAM_REALM));
                parameters.put(SessionParameter.USER, repositoryConfig.get(ConfigureCMISPluginAction.PARAM_USERNAME));
                parameters.put(SessionParameter.PASSWORD, repositoryConfig.get(ConfigureCMISPluginAction.PARAM_PASWORD));

                List<Repository> repos = SessionFactoryImpl.newInstance().getRepositories(parameters);

                // TODO: how to choose one?
                this.repositories.put(repoName, repos.get(0));
            }
            else
            {
                throw new NoRepositoryException();
            }
        }

        return this.repositories.get(repoName);
    }
    /*
        public Repository getRepository(String serverUrl, String username, String password) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            ContentManager cm = new APPContentManager(serverUrl);
            cm.login(credentials.getUserName(), credentials.getPassword());
            return cm.getDefaultRepository();
        }
    */

    public Set<String> getRepositoryNames()
    {
        return getRepositoriesMap().keySet();
    }

    private void setBandanaManager(BandanaManager bandanaManager)
    {
        this.bandanaManager = bandanaManager;
    }

}
