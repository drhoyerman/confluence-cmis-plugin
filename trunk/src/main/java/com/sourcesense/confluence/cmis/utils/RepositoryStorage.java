package com.sourcesense.confluence.cmis.utils;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Implements a simple cache logic to handle repositories
 */
@SuppressWarnings("unused,unchecked")
public class RepositoryStorage {

  protected static final Logger logger = Logger.getLogger(RepositoryStorage.class);

  private Map<String, ConfluenceCMISRepository> repositories;

  //Singleton
  private static RepositoryStorage repositoryStorage;

  public static RepositoryStorage getInstance(BandanaManager bandanaManager) {
    if (repositoryStorage == null) {
      repositoryStorage = new RepositoryStorage(bandanaManager);
    }
    return repositoryStorage;
  }

  public RepositoryStorage(BandanaManager bandanaManager) {
    ConfluenceBandanaContext context = new ConfluenceBandanaContext();
    Map storedConfiguration = (Map)bandanaManager.getValue(context, ConfigureCMISPluginAction.CREDENTIALS_KEY);

    //Ensure that the configuration object contains a Map<String, ConfluenceCMISRepository>
    boolean resetConfiguration = false;
    if (storedConfiguration.keySet().size() > 0) {
      Object firstKey = storedConfiguration.keySet().iterator().next();
      Object firstVal = storedConfiguration.get(firstKey);
      if (firstKey instanceof String && firstVal instanceof ConfluenceCMISRepository) {
        this.repositories = (Map<String, ConfluenceCMISRepository>)storedConfiguration;
      } else {
        resetConfiguration = true;
      }
    } else {
      resetConfiguration = true;
    }

    if (resetConfiguration) {
      bandanaManager.setValue(context, ConfigureCMISPluginAction.CREDENTIALS_KEY, null);
      this.repositories = new WeakHashMap<String, ConfluenceCMISRepository>();
    }
  }

  public ConfluenceCMISRepository getRepository() throws CmisRuntimeException {
    if (repositories.entrySet().isEmpty()) {
      throw new CmisRuntimeException("Could not find any CMIS repository; check your plugin configuration.");
    } else {
      String firstServerName = this.repositories.keySet().iterator().next();
      return this.repositories.get(firstServerName);
    }
  }

  public ConfluenceCMISRepository getRepository(String serverName) throws CmisRuntimeException {
    if (!repositories.containsKey(serverName)) {
      throw new CmisRuntimeException("Could not find a CMIS repository with name " + serverName + "; check your plugin configuration.");
    } else {
      return this.repositories.get(serverName);
    }
  }

  public Set<String> getRepositoryNames() {
    return repositories.keySet();
  }
}
