/*
 * Copyright 2010 Sourcesense <http://www.sourcesense.com>
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
package com.sourcesense.confluence.cmis;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import junit.framework.TestCase;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;

import java.util.*;

public class TestBaseCMISMacro extends TestCase {
  Logger logger = Logger.getLogger(TestBaseCMISMacro.class);

  String cmisRealm = "http://cmis.alfresco.com:80/service/cmis";
  String cmisUser = "admin";
  String cmisPwd = "admin";

  public void testRepositoryConnection() throws Exception {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("servername", "test");

    /**
         * You can use -Drealm -Duser and -Dpwd in order to override the default values
       **/
    String cmisRealmProp = System.getProperty("realm");
    if (cmisRealmProp != null) cmisRealm = cmisRealmProp;
    String cmisUserProp = System.getProperty("user");
    if (cmisUserProp != null) cmisUser = cmisUserProp;
    String cmisPwdProp = System.getProperty("pwd");
    if (cmisPwdProp != null) cmisPwd = cmisPwdProp;

    BaseCMISMacro baseMacro = new BaseCMISMacro() {

      public boolean hasBody() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public RenderMode getBodyRenderMode() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      @Override
      protected String executeImpl(Map params, String body, RenderContext renderContext, Repository repo) {
        return "OK";
      }
    };

    baseMacro.setBandanaManager(new MockBandanaManager());

    String result = null;

    try {
      result = baseMacro.execute(parameters, null, null);
    }
    catch (MacroException me) {
      logger.error(me);
      fail(me.getMessage());
    }

    assertNotNull(result);
    assertFalse("".equals(result));

    logger.debug(result);
  }

  public void testRepositoryEnumeration() {

    RepositoryStorage repoStorage = RepositoryStorage.getInstance(new MockBandanaManager());

    Set<String> repos = repoStorage.getRepositoryNames();

    assertNotNull(repos);
    assertTrue(!repos.isEmpty());

    for (String repo : repos) {
      try {
        Repository repoDesc = repoStorage.getRepository(repo);

        logger.debug("name: " + repoDesc.getName());
        logger.debug("id: " + repoDesc.getId());
        logger.debug("productName : " + repoDesc.getProductName());
        logger.debug("cmisVersionSupported: " + repoDesc.getCmisVersionSupported());
        logger.debug("description: " + repoDesc.getDescription());
      }
      catch (CmisRuntimeException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        fail(e.getMessage());
      }
    }

  }

  public void testCMISLinkGeneration() {
    RepositoryStorage repoStorage = RepositoryStorage.getInstance(new MockBandanaManager());

    try {
      Repository repo = repoStorage.getRepository("test");
      Session session = repo.createSession();
      ItemIterable<CmisObject> children = session.getRootFolder().getChildren();
      for (CmisObject obj : children) {
        if ("TODO.txt".equals(obj.getName())) {
          Document doc = (Document) obj;
          System.out.println(doc);
        }
      }
    }
    catch (CmisRuntimeException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      fail(e.getMessage());
    }
  }

  class MockBandanaManager implements BandanaManager {

    public void init() {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setValue(BandanaContext bandanaContext, String s, Object o) {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    // used in RepositoryStorage

    public Object getValue(BandanaContext bandanaContext, String s) {
      Map<String, List<String>> repoConfigs = new WeakHashMap<String, List<String>>();
      List<String> repoConfig = new ArrayList<String>();

      repoConfig.add(cmisRealm);
      repoConfig.add(cmisUser);
      repoConfig.add(cmisPwd);
      //No need to specify a RepositoryID
      repoConfig.add(null);

      repoConfigs.put("test", repoConfig);

      return repoConfigs;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getValue(BandanaContext bandanaContext, String s, boolean b) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String exportValues(BandanaContext bandanaContext) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void importValues(BandanaContext bandanaContext, String s) {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }
}
