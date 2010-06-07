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

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.utils.ConfluenceCMISRepository;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestBaseCMISMacro extends AbstractBaseUnitTest {

  Logger logger = Logger.getLogger(TestBaseCMISMacro.class);

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
}
