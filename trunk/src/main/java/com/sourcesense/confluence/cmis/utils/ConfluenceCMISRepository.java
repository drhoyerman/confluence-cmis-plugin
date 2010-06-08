package com.sourcesense.confluence.cmis.utils;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;

public class ConfluenceCMISRepository {
  private String name;
  private Session session;

  public ConfluenceCMISRepository(String name, Repository repository) {
    this.name = name;
    this.session = repository.createSession();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Session getSession() {
    return session;
  }

}
