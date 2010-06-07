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
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.utils.ConfluenceCMISRepository;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import junit.framework.TestCase;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestBaseCMISMacro extends TestCase {

  Logger logger = Logger.getLogger(TestBaseCMISMacro.class);

  protected VelocityEngine ve;
  protected VelocityContext vc;
  protected BandanaManager bandanaManager;

  String cmisRealm = "http://cmis.alfresco.com:80/service/cmis";
  String cmisUser = "admin";
  String cmisPwd = "admin";

  public void setUp () throws Exception
  {
      super.setUp();

      // log4j:
      PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));

      // Velocity:
      Properties p = new Properties();
      p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
      p.setProperty("resource.loader", "class");
      p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
      p.setProperty("runtime.log.logsystem.log4j.category", "velocity");

      vc = new VelocityContext();
      ve = new VelocityEngine();
      ve.init(p);

      // Confluence
      Map<String, List<String>> repoConfigs = new WeakHashMap<String, List<String>>();
      List<String> repoConfig = new ArrayList<String>();

      repoConfig.add(cmisRealm);
      repoConfig.add(cmisUser);
      repoConfig.add(cmisPwd);
      //No need to specify a RepositoryID
      repoConfig.add(null);
      repoConfigs.put("test", repoConfig);

      bandanaManager = mock (BandanaManager.class);
      when(bandanaManager.getValue((BandanaContext)anyObject(), anyString())).thenReturn(repoConfigs);
  }

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

    BaseCMISMacro baseMacro = mock (BaseCMISMacro.class);
    when(baseMacro.execute(anyMap(), anyString(), (RenderContext)anyObject())).thenReturn("OK");
    when(baseMacro.hasBody()).thenReturn(false);
    baseMacro.setBandanaManager(bandanaManager);

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

    RepositoryStorage repoStorage = RepositoryStorage.getInstance(bandanaManager);

    Set<String> repos = repoStorage.getRepositoryNames();

    assertNotNull(repos);
    assertTrue(!repos.isEmpty());

    for (String repo : repos) {
      try {
        ConfluenceCMISRepository repoDesc = repoStorage.getRepository(repo);

        logger.debug("name: " + repoDesc.getName());
        logger.debug("id: " + repoDesc.getRepository().getId());
        logger.debug("productName : " + repoDesc.getRepository().getProductName());
        logger.debug("cmisVersionSupported: " + repoDesc.getRepository().getCmisVersionSupported());
        logger.debug("description: " + repoDesc.getRepository().getDescription());
      }
      catch (CmisRuntimeException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        fail(e.getMessage());
      }
    }

  }

  public void testCMISLinkGeneration() {
    RepositoryStorage repoStorage = RepositoryStorage.getInstance(bandanaManager);

    try {
      ConfluenceCMISRepository repo = repoStorage.getRepository("test");
      Session session = repo.getRepository().createSession();
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
}
