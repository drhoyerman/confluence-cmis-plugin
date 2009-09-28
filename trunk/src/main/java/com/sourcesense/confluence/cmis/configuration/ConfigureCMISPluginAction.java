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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;

public class ConfigureCMISPluginAction extends ConfluenceActionSupport {

    public static final String CREDENTIALS_KEY = ConfigureCMISPluginAction.class.getPackage().getName() + ".credential";
    private BandanaManager bandanaManager;
    private ConfluenceBandanaContext context = new ConfluenceBandanaContext();

    private Map<String, List<String>> credentialsMap = new TreeMap<String, List<String>>();
    private String[] realms;
    private String[] usernames;
    private String[] passwords;
    private String[] servernames;
    private int indexToDelete = -1;

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @SuppressWarnings("unchecked")
    public String input() {
        this.credentialsMap = (Map<String, List<String>>) this.bandanaManager.getValue(context, CREDENTIALS_KEY);
        return INPUT;
    }

    public String save() throws Exception {
        if (hasErrors()) {
            return ERROR;
        }
        this.credentialsMap = convertToCredentialsMap();
        this.bandanaManager.setValue(context, CREDENTIALS_KEY, this.credentialsMap);
        addActionMessage("Successfully saved configuration");
        return SUCCESS;
    }

    public String add() {
        this.credentialsMap = convertToCredentialsMap();
        List<String> list = new ArrayList<String>();
        list.add("http://");
        list.add("");
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

    public void setIndexToDelete(int indexToDelete) {
        this.indexToDelete = indexToDelete;
    }
}
