package com.sourcesense.confluence.cmis.utils;

import com.sourcesense.confluence.cmis.BaseCMISMacro;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfluenceCMISRepository {

  protected static final Logger logger = Logger.getLogger(ConfluenceCMISRepository.class);
  
  private String serverName;
  private Map<String, String> parameters = new HashMap<String, String>();
  private Session session;

  public ConfluenceCMISRepository(String serverName, String realm, String username, String password, String repositoryId) {
    this.serverName = serverName;
    parameters.put(BaseCMISMacro.REPOSITORY_NAME, serverName);
    parameters.put(SessionParameter.REPOSITORY_ID, repositoryId);
    parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    parameters.put(SessionParameter.ATOMPUB_URL, realm);
    parameters.put(SessionParameter.USER, username);
    parameters.put(SessionParameter.PASSWORD, password);

    this.session = createSession();
  }

  public String getServerName() {
    return serverName;
  }

  public String getParameter(String parameterName) {
    return parameters.get(parameterName);
  }

  public void setParameter(String parameterName, String parameterValue) {
    parameters.put(parameterName, parameterValue);
  }

  public Session getSession() {
    return session;
  }

  private Session createSession() {
    String repositoryId = parameters.get(SessionParameter.REPOSITORY_ID);
    if (repositoryId != null && !repositoryId.isEmpty()) {
      parameters.put(SessionParameter.REPOSITORY_ID, repositoryId);
    }
    List<Repository> repos = SessionFactoryImpl.newInstance().getRepositories(parameters);

    if (repos == null || repos.size() <= 0) {
      parameters.remove(SessionParameter.PASSWORD);
      parameters.put(SessionParameter.PASSWORD, "*********");
      throw new CmisRuntimeException("Could not retrieve any CMIS repository with the following parameters: " + parameters);
    }

    if (!repos.isEmpty()) {
      Repository repo = repos.iterator().next();
      if (repos.size() > 1) {
        logger.warn("There is more than one repository supported in this realm; you should define a Repository Id in your configuration; currently, the first is used; Repository ID : " + repo.getId());
      }
      return repo.createSession();
    } else {
      throw new CmisRuntimeException("No CMIS repositories found matching the given parameters: " + parameters);
    }
  }
}
