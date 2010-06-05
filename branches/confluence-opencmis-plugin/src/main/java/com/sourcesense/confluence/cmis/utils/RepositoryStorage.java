package com.sourcesense.confluence.cmis.utils;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Implements a simple cache logic to handle repositories
 */
public class RepositoryStorage {

  protected static final Logger logger = Logger.getLogger(RepositoryStorage.class);

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
   * @throws CmisRuntimeException
   */
  public Repository getRepository(String repoName) throws CmisRuntimeException {
    if (!repositories.containsKey(repoName)) {
      List<String> repositoryConfig = getRepositoriesMap().get(repoName);
      if (repositoryConfig != null) {
        Repository repo = getCMISRepository(repositoryConfig.get(ConfigureCMISPluginAction.PARAM_REALM),
            repositoryConfig.get(ConfigureCMISPluginAction.PARAM_USERNAME),
            repositoryConfig.get(ConfigureCMISPluginAction.PARAM_PASSWORD),
            repositoryConfig.get(ConfigureCMISPluginAction.PARAM_REPOID));
        this.repositories.put(repoName, repo);
      } else {
        throw new CmisRuntimeException(String.format("No repository found with name '%s'; check the Plugin configuration.", repoName));
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
   * @throws CmisRuntimeException
   */
  private Repository getCMISRepository(String serverUrl, String username, String password, String repositoryId) throws CmisRuntimeException {
    Map<String, String> parameters = new HashMap<String, String>();

    parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    parameters.put(SessionParameter.ATOMPUB_URL, serverUrl);
    parameters.put(SessionParameter.USER, username);
    parameters.put(SessionParameter.PASSWORD, password);

    if (repositoryId != null && !repositoryId.isEmpty()) {
      parameters.put(SessionParameter.REPOSITORY_ID, repositoryId);
    }

    List<Repository> repos = SessionFactoryImpl.newInstance().getRepositories(parameters);

    if (repos == null || repos.size() <= 0) {
      parameters.remove(SessionParameter.PASSWORD);
      parameters.put(SessionParameter.PASSWORD, "*********");
      throw new CmisRuntimeException("Could not retrieve any CMIS repository with the following parameters: " + parameters);
    }

    Repository repo = repos.get(0);
    if (repos.size() > 1) {
      logger.warn("There is more than one repository supported in this realm; you should define a Repository Id in your configuration; currently, the first is used; Repository ID : " + repo.getId());
    }

    return repo;

  }


  public Set<String> getRepositoryNames() {
    return getRepositoriesMap().keySet();
  }

  private void setBandanaManager(BandanaManager bandanaManager) {
    this.bandanaManager = bandanaManager;
  }

  public Repository getRepository() {
    if (getRepositoryNames().isEmpty())
      throw new CmisRuntimeException("No CMIS repositories configured! Check the Plugin configuration.");
    String firstRepository = getRepositoryNames().iterator().next();
    logger.info("No Repository specified; using the first in the list : " + firstRepository);
    return getRepository(firstRepository);
  }
}
