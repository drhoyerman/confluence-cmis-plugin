package com.sourcesense.confluence.cmis.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.connector.APPContentManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import com.sourcesense.confluence.cmis.exception.NoRepositoryException;

public class RepositoryStorage {

    private ConfluenceBandanaContext context = new ConfluenceBandanaContext();
    private Map<String, Repository> repositories;
    private BandanaManager bandanaManager;
    private static RepositoryStorage repositoryStorage;

    public static RepositoryStorage getInstance(BandanaManager bandanaManager) {
        if (repositoryStorage == null) {
            repositoryStorage = new RepositoryStorage();
        }
        repositoryStorage.setBandanaManager(bandanaManager); // XXX is necessary every time?
        return repositoryStorage;
    }

    public static RepositoryStorage resetAndGetInstance(Map<String, Repository> cache, BandanaManager bandanaManager) {
        repositoryStorage = new RepositoryStorage(cache);
        return getInstance(bandanaManager);
    }

    public RepositoryStorage() {
        this.repositories = new WeakHashMap<String, Repository>();
    }

    public RepositoryStorage(Map<String, Repository> cache) {
        this.repositories = cache;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getRepositoriesMap() {
        return ((Map<String, List<String>>) this.bandanaManager.getValue(context, ConfigureCMISPluginAction.CREDENTIALS_KEY));
    }

    /**
     * @param repoName The name configured by confluence admin
     * @return The CMIS repository if exists or null
     *
     */
    public Repository getRepository(String repoName) throws NoRepositoryException {
        if (!repositories.containsKey(repoName)) {
            List<String> repositoryList = getRepositoriesMap().get(repoName);
            if (repositoryList != null) {
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(repositoryList.get(1), repositoryList.get(2));
                ContentManager cm = new APPContentManager(repositoryList.get(0));
                cm.login(credentials.getUserName(), credentials.getPassword());
                this.repositories.put(repoName, cm.getDefaultRepository());
            } else {
                throw new NoRepositoryException();
            }
        }
        return this.repositories.get(repoName);
    }

    public Repository getRepository(String serverUrl, String username, String password) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        ContentManager cm = new APPContentManager(serverUrl);
        cm.login(credentials.getUserName(), credentials.getPassword());
        return cm.getDefaultRepository();
    }

    public Set<String> getRepositoryNames() {
        return getRepositoriesMap().keySet();
    }

    private void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

}
