package com.sourcesense.confluence.cmis.utils;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

import java.util.*;

/**
 * Implements a simple cache logic to handle repositories
 */
public class RepositoryStorage {

  private ConfluenceBandanaContext context = new ConfluenceBandanaContext();
  private Map<String, Repository> repositories;
  private BandanaManager bandanaManager;
  private static RepositoryStorage repositoryStorage;

  public static RepositoryStorage getInstance(BandanaManager bandanaManager) {
    if (repositoryStorage == null) {
      repositoryStorage = new RepositoryStorage();
      repositoryStorage.setBandanaManager(bandanaManager);
    }

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
   * Gets a repository fetching its details from the plugin configuration using repoName as the key
   *
   * @param repoName Rpository ID as it was set in the plugin configuration
   * @return
   * @throws NoRepositoryException
   */
  public Repository getRepository(String repoName) throws CmisRuntimeException {
    if (!repositories.containsKey(repoName)) {
      List<String> repositoryConfig = getRepositoriesMap().get(repoName);
      if (repositoryConfig != null) {
        Repository repo = getRepository(repositoryConfig.get(ConfigureCMISPluginAction.PARAM_REALM),
            repositoryConfig.get(ConfigureCMISPluginAction.PARAM_USERNAME),
            repositoryConfig.get(ConfigureCMISPluginAction.PARAM_PASSWORD));

        // TODO: how to choose one?
        this.repositories.put(repoName, repo);
      } else {
        throw new CmisRuntimeException("No repository found");
      }
    }

    return this.repositories.get(repoName);
  }

  /**
   * Gets the repository identified by the provided coordinates
   *
   * @param serverUrl URL where the AtomPub CMIS repository service listens
   * @param username  Username used to log into the repository
   * @param password  Password used to log into the repository
   * @return
   * @throws NoRepositoryException
   */
  public Repository getRepository(String serverUrl, String username, String password) throws CmisRuntimeException {
    Map<String, String> parameters = new HashMap<String, String>();

    parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    parameters.put(SessionParameter.ATOMPUB_URL, serverUrl);
    parameters.put(SessionParameter.USER, username);
    parameters.put(SessionParameter.PASSWORD, password);

    List<Repository> repos = SessionFactoryImpl.newInstance().getRepositories(parameters);

    if (repos == null || repos.size() <= 0) {
      throw new CmisRuntimeException("No repository found");
    }

    // TODO: choose the repo in a better way
    return repos.get(0);
  }


  public Set<String> getRepositoryNames() {
    return getRepositoriesMap().keySet();
  }

  private void setBandanaManager(BandanaManager bandanaManager) {
    this.bandanaManager = bandanaManager;
  }

}
