package com.sourcesense.confluence.cmis.utils;

import org.apache.chemistry.opencmis.client.api.Repository;

public class ConfluenceCMISRepository {
  private String name;
  private Repository repository;

  public ConfluenceCMISRepository(String name, Repository repository) {
    this.name = name;
    this.repository = repository;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }
}
