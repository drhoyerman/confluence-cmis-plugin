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
import com.sourcesense.confluence.cmis.utils.Utils;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.log4j.Logger;

import java.util.*;

public class SearchMacro extends BaseCMISMacro {
  protected static final Logger logger = Logger.getLogger(SearchMacro.class);

  @Override
  public boolean hasBody() {
    return true;
  }

  @Override
  protected String executeImpl(Map params, String body, RenderContext renderContext, ConfluenceCMISRepository confluenceCmisRepository) throws MacroException {

    Session session = confluenceCmisRepository.getSession();
    boolean useProxy = (Boolean) params.get(BaseCMISMacro.PARAM_USEPROXY);

    List<String> properties = getProperties(params.get(BaseCMISMacro.PARAM_PROPERTIES));

    ItemIterable<QueryResult> results = session.query(body, false);
    StringBuilder out = new StringBuilder();

    renderResults(out, results, properties, confluenceCmisRepository, session, useProxy);

    logger.debug("results:");
    logger.debug(out.toString());
    return out.toString();
  }

  private void renderResults(StringBuilder out, ItemIterable<QueryResult> results, List<String> properties, ConfluenceCMISRepository confluenceCmisRepository, Session session, boolean useProxy) throws MacroException {
    renderTableHeader(out, properties);
    for (QueryResult res : results) {
      renderResult(out, res, properties, confluenceCmisRepository, session, useProxy);
    }
  }

  private void renderResult(StringBuilder out, QueryResult queryResult, List<String> properties, ConfluenceCMISRepository confluenceCmisRepository, Session session, boolean useProxy) throws MacroException {

    //Defining a map id -> value for all properties of the current result; easier to handle
    Map<String, List> cmisQueryProperties = new HashMap<String, List>();
    for (PropertyData prop : queryResult.getProperties()) {
      cmisQueryProperties.put(prop.getId(), prop.getValues());
    }

    // replace the node name with a wiki-style link
    List listVal = cmisQueryProperties.get(PropertyIds.NAME);
    String objectId = (String) queryResult.getPropertyById(PropertyIds.OBJECT_ID).getFirstValue();
    String name = (String) listVal.get(0);
    String link = Utils.getLink(session, confluenceCmisRepository, objectId, useProxy);
    listVal.set(0, String.format("[%s|%s]", name, link));

    //Displaying the 3 principal and mandatory columns
    renderProperty(out, PropertyIds.NAME, cmisQueryProperties);
    renderProperty(out, PropertyIds.LAST_MODIFICATION_DATE, cmisQueryProperties);
    renderProperty(out, PropertyIds.CONTENT_STREAM_LENGTH, cmisQueryProperties);

    //Displaying the additional and configurable columns
    for (String property : properties) {
      renderProperty(out, property, cmisQueryProperties);
    }

    //End of a table row
    out.append("|");
    out.append("\n");
  }

  private void renderProperty(StringBuilder out, String property, Map<String, List> cmisPropMap) {
    List valueList = cmisPropMap.get(property);
    if (valueList != null && valueList.size() > 0) {
      out.append("|");
      Iterator i = valueList.iterator();
      while (i.hasNext()) {
        renderPropertyValue(out, i.next());
        if (i.hasNext()) {
          out.append(";");
        }
      }
    }
  }

  private void renderPropertyValue(StringBuilder out, Object o) {
    if (o instanceof Calendar) {
      Calendar cal = (Calendar) o;
      out.append(sdf.format(cal.getTime()));
    } else {
      out.append(o);
    }
  }

  private void renderTableHeader(StringBuilder out, List<String> columns) {
    out.append("||Title||Last Modified||Size||");
    if (columns.size() > 0) {
      for (String prop : columns) {
        out.append(getCMISPropertyTitle(prop));
        out.append("||");
      }
    }
    out.append("\n");
  }

  private String getCMISPropertyTitle(String property) {
    String title = getCMISPropertyName(property);
    return Character.toUpperCase(title.charAt(0)) + title.substring(1);
  }

  private String getCMISPropertyName(String property) {
    return property.substring(property.lastIndexOf(':')+1);
  }

  /**
   * Retrieve the list of properties the user wants to show. If they're not overridden within the macro parameters,
   * the global plugin configuration is used as a default
   *
   * @param properties Property list as passed to the macro
   * @return The input properties or the saved onse in case of no 'properties' macro parameter
   */
  private List<String> getProperties(Object properties) {
    if (properties instanceof String && !((String) properties).isEmpty()) {
      return Arrays.asList(((String) properties).split(";"));
    }
    logger.debug("Fetching the properties from the global plugin configuration");

    // get the properties enumeration from the plugin conf
    return (List<String>) this.bandanaManager.getValue(new ConfluenceBandanaContext(), ConfigureCMISPluginAction.SEARCH_PROPERTIES_KEY);

  }
}