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

import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import com.sourcesense.confluence.cmis.utils.ConfluenceCMISRepository;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SearchMacro extends BaseCMISMacro {
  protected static final Logger logger = Logger.getLogger(SearchMacro.class);

  @Override
  public boolean hasBody() {
    return true;
  }

  @Override
  protected String executeImpl(Map params, String body, RenderContext renderContext, ConfluenceCMISRepository confluenceCmisRepository) throws MacroException {

    Session session = confluenceCmisRepository.getSession();

    List<String> configuredProperties = getProperties(params.get(BaseCMISMacro.PARAM_PROPERTIES));
    List<String> properties = new ArrayList<String> ();
      
    properties.add(PropertyIds.NAME);
    properties.addAll(configuredProperties);

    ItemIterable<QueryResult> results = session.query(body, false);
    
    renderContext.addParam(VM_CMIS_OBJECT_LIST, results);
    renderContext.addParam(VM_CMIS_PROPERTY_LIST, properties);

    return render(getTemplate(), renderContext);
  }

  /**
   * Retrieve the list of properties the user wants to show. If they're not overridden within the macro parameters,
   * the global plugin configuration is used as a default
   *
   * @param properties Property list as passed to the macro
   * @return The input properties or the saved ones in case of no 'properties' macro parameter
   */
  @SuppressWarnings("unchecked")
  private List<String> getProperties(Object properties) {
    if (properties instanceof String && !((String) properties).isEmpty()) {
      return Arrays.asList(((String) properties).split(";"));
    }
    logger.debug("Fetching the properties from the global plugin configuration");

    // get the properties enumeration from the plugin conf
    return (List<String>) this.bandanaManager.getValue(new ConfluenceBandanaContext(), ConfigureCMISPluginAction.SEARCH_PROPERTIES_KEY);

  }

  protected String getTemplate ()
  {
      return "templates/cmis/search.vm";
  }
}