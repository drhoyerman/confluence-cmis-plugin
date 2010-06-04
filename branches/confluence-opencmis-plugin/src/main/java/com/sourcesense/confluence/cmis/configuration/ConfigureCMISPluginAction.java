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
package com.sourcesense.confluence.cmis.configuration;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import java.util.*;

public class ConfigureCMISPluginAction extends ConfluenceActionSupport {

  public static final String CREDENTIALS_KEY = ConfigureCMISPluginAction.class.getPackage().getName() + ".credential";
  public static final String SEARCH_PROPERTIES_KEY = ConfigureCMISPluginAction.class.getPackage().getName() + ".searchProperties";

  public static final Integer PARAM_REALM = 0;
  public static final Integer PARAM_USERNAME = 1;
  public static final Integer PARAM_PASSWORD = 2;
  public static final Integer PARAM_REPOID = 3;

  private BandanaManager bandanaManager;
  private ConfluenceBandanaContext context = new ConfluenceBandanaContext();

  private Map<String, List<String>> credentialsMap = new TreeMap<String, List<String>>();
  private List<String> searchProperties = new LinkedList<String>();
  private String[] realms;
  private String[] usernames;
  private String[] passwords;
  private String[] repoids;
  private String[] servernames;
  private int indexToDelete = -1;

  public void setBandanaManager(BandanaManager bandanaManager) {
    this.bandanaManager = bandanaManager;
  }

  @SuppressWarnings("unchecked")
  public String input() {
    this.credentialsMap = (Map<String, List<String>>) this.bandanaManager.getValue(context, CREDENTIALS_KEY);
    this.searchProperties = (List<String>) this.bandanaManager.getValue(context, SEARCH_PROPERTIES_KEY);
    return INPUT;
  }

  public String save() throws Exception {
    if (hasErrors()) {
      return ERROR;
    }
    this.credentialsMap = convertToCredentialsMap();
    this.bandanaManager.setValue(context, CREDENTIALS_KEY, this.credentialsMap);
    this.bandanaManager.setValue(context, SEARCH_PROPERTIES_KEY, this.searchProperties);
    addActionMessage("Successfully saved configuration");
    return SUCCESS;
  }

  public String add() {
    this.credentialsMap = convertToCredentialsMap();
    List<String> list = new ArrayList<String>();
    list.add("http://");
    list.add("admin");
    list.add("admin");
    list.add("");
    this.credentialsMap.put("insert server name", list);
    return SUCCESS;
  }

  public String delete() {
    this.credentialsMap = convertToCredentialsMap();
    if (indexToDelete < 0 || indexToDelete >= this.credentialsMap.size()) {
      addActionError(getText("invalid.index.to.delete"));
      return ERROR;
    }
    this.credentialsMap = convertToCredentialsMap(indexToDelete);
    this.bandanaManager.setValue(context, CREDENTIALS_KEY, this.credentialsMap);
    addActionMessage("Successfully deleted configuration");
    return SUCCESS;
  }

  private Map<String, List<String>> convertToCredentialsMap() {
    return convertToCredentialsMap(-1);
  }

  private Map<String, List<String>> convertToCredentialsMap(int indexToDelete) {
    Map<String, List<String>> map = new TreeMap<String, List<String>>();
    for (int i = 0; i < servernames.length; ++i) {
      if (i != indexToDelete && !"".equals(servernames[i].trim())) {
        List<String> list = new ArrayList<String>();
        list.add(realms[i]);
        list.add(usernames[i]);
        list.add(passwords[i]);
        list.add(repoids[i]);
        map.put(servernames[i], list);
      }
    }
    return map;
  }

  public Map<String, List<String>> getCredentials() {
    return this.credentialsMap;
  }

  public void setRealms(String[] realms) {
    this.realms = realms;
  }

  public void setUsernames(String[] usernames) {
    this.usernames = usernames;
  }

  public void setPasswords(String[] passwords) {
    this.passwords = passwords;
  }

  public void setServernames(String[] servernames) {
    this.servernames = servernames;
  }

  public void setRepoids(String[] repoids) {
    this.repoids = repoids;
  }

  public void setIndexToDelete(int indexToDelete) {
    this.indexToDelete = indexToDelete;
  }

  public boolean hasName() {
    return this.searchProperties.contains(PropertyIds.NAME);
  }
  public void setName(String name) {
    this.searchProperties.add(PropertyIds.NAME);
  }

  public boolean hasObjectId() {
    return this.searchProperties.contains(PropertyIds.OBJECT_ID);
  }
  public void setObjectId(String objectId) {
    this.searchProperties.add(PropertyIds.OBJECT_ID);
  }

  public boolean hasObjectTypeId() {
    return this.searchProperties.contains(PropertyIds.OBJECT_TYPE_ID);
  }
  public void setObjectTypeId(String objectTypeId) {
    this.searchProperties.add(PropertyIds.OBJECT_TYPE_ID);
  }

  public boolean hasCreatedBy() {
    return this.searchProperties.contains(PropertyIds.CREATED_BY);
  }
  public void setCreatedBy(String createdBy) {
    this.searchProperties.add(PropertyIds.CREATED_BY);
  }

  public boolean hasCreationDate() {
    return this.searchProperties.contains(PropertyIds.CREATION_DATE);
  }
  public void setCreationDate(String creationDate) {
    this.searchProperties.add(PropertyIds.CREATION_DATE);
  }


  public boolean hasLastModifiedBy() {
    return this.searchProperties.contains(PropertyIds.LAST_MODIFIED_BY);
  }
  public void setLastModifiedBy(String lastModifiedBy) {
    this.searchProperties.add(PropertyIds.LAST_MODIFIED_BY);
  }

  public boolean hasLastModificationDate() {
    return this.searchProperties.contains(PropertyIds.LAST_MODIFICATION_DATE);
  }
  public void setLastModificationDate(String lastModificationDate) {
    this.searchProperties.add(PropertyIds.LAST_MODIFICATION_DATE);
  }

  public boolean hasIsLatestVersion() {
    return this.searchProperties.contains(PropertyIds.IS_LATEST_VERSION);
  }
  public void setIsLatestVersion(String isLatestVersion) {
    this.searchProperties.add(PropertyIds.IS_LATEST_VERSION);
  }

}
