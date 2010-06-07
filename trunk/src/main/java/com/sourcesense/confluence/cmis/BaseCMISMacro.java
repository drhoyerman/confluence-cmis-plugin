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

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.utils.ConfluenceCMISRepository;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Map;

public abstract class BaseCMISMacro extends BaseMacro {
  protected static final Logger logger = Logger.getLogger(BaseCMISMacro.class);

  protected static final String PARAM_REPOSITORY_ID = "servername";
  protected static final String PARAM_ID = "id";
  protected static final String PARAM_RESULTS_NUMBER = "maxResults";
  protected static final String PARAM_NOFORMAT = "nf";
  protected static final String PARAM_USEPROXY = "useproxy";
  protected static final String PARAM_PROPERTIES = "properties";

  public static final java.lang.String REPOSITORY_NAME = "com.sourcesense.confluence.cmis.repository.name";

  protected static final int DEFAULT_RESULTS_NUMBER = 20;
  protected static final String DEFAULT_USEPROXY = "yes";

  //TODO : make this regexp configurable, along with the locale
  protected static SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");

  protected BandanaManager bandanaManager;
  protected SettingsManager settingsManager;

  public void setBandanaManager(BandanaManager bandanaManager) {
    this.bandanaManager = bandanaManager;
  }

  public void setSettingsManager(SettingsManager settingsManager) {
    this.settingsManager = settingsManager;
  }

  @SuppressWarnings("unchecked")
  public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
    // Work around to let OpenCMIS using the bundled version of Jaxb
    ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

    try {
      ClassLoader cl = this.getClass().getClassLoader();
      Thread.currentThread().setContextClassLoader(cl);

      RepositoryStorage repositoryStorage = RepositoryStorage.getInstance(bandanaManager);
      ConfluenceCMISRepository repositoryConfluence = getRepositoryFromParams(params, repositoryStorage);

      populateParams(params, body, renderContext, repositoryConfluence);

      return executeImpl(params, body, renderContext, repositoryConfluence);
    }
    catch (Exception e) {
      logger.error("Cannot open a session with the CMIS repository", e);
      e.printStackTrace();
      throw new MacroException(e);
    }
    finally {
      Thread.currentThread().setContextClassLoader(originalClassLoader);
    }
  }

  /**
   * Parses and provides common casting for mosly used and wide-spread macro parameters
   * @param params
   * @param body
   * @param renderContext
   * @param repositoryConfluence
   */
  private void populateParams(Map params, String body, RenderContext renderContext, ConfluenceCMISRepository repositoryConfluence) {
    String useProxy = (String) params.get(BaseCMISMacro.PARAM_USEPROXY);
    if (useProxy == null || useProxy.isEmpty()) {
      useProxy = BaseCMISMacro.DEFAULT_USEPROXY;
    }
    if (useProxy.equals("y") || useProxy.equals("yes")) {
      params.put(BaseCMISMacro.PARAM_USEPROXY, Boolean.TRUE);
    } else {
      params.put(BaseCMISMacro.PARAM_USEPROXY, Boolean.FALSE);
    }
  }

  protected abstract String executeImpl(Map params, String body, RenderContext renderContext, ConfluenceCMISRepository repositoryConfluence) throws MacroException;
  
  /**
   * Retrieves a Repository descriptor depending by the macro parameters:
   * - The user must provide the repository id ("servername") than the repository details are taken from the plugin configuration
   *
   * @param params            Parameter map holding the macro parameters
   * @param repositoryStorage Cached repository retriever
   * @return
   * @throws CmisRuntimeException
   */
  protected ConfluenceCMISRepository getRepositoryFromParams(Map params, RepositoryStorage repositoryStorage) throws CmisRuntimeException {
    String repoId = (String) params.get(PARAM_REPOSITORY_ID);
    if (repoId != null && !repoId.isEmpty()) {
      return repositoryStorage.getRepository(repoId);
    } else return repositoryStorage.getRepository();
  }

  public RenderMode getBodyRenderMode() {
    return null;
  }

  public boolean hasBody() {
    return false;
  }
  
}
