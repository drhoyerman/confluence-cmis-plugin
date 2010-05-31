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
package com.sourcesense.confluence.cmis;

import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import com.sourcesense.confluence.cmis.utils.Utils;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.impl.Constants;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.util.*;

public class SearchMacro extends BaseCMISMacro
{
    private static final Logger logger = Logger.getLogger ("com.sourcesense.confluence.cmis");

    public static String PARAM_PROPERTIES = "properties";

    public boolean hasBody()
    {
        return true;
    }

    public RenderMode getBodyRenderMode()
    {
        return null;
    }

    @Override
    protected String executeImpl(Map params, String body, RenderContext renderContext, Repository repository)
    {

        Session session = repository.createSession();

        List<String> properties = getProperties(params.get(PARAM_PROPERTIES));
        logger.debug("Iterating over the following properties:");
        for (String prop : properties)
        {
            logger.debug("\t - " + prop);
        }

        ItemIterable<QueryResult> results = session.query(body, false);
        StringBuilder out = new StringBuilder();

        renderResults(out, results, properties, repository);

        logger.debug ("result:");
        logger.debug (out.toString());

        return out.toString();
    }

    private void renderResults(StringBuilder out, ItemIterable<QueryResult> results, List<String> properties, Repository repository)
    {
        renderTitle(out, properties);

        out.append("|");
        for (QueryResult res : results)
        {
            renderEntry(out, res, properties, repository);
        }
    }

    private void renderEntry(StringBuilder out, QueryResult res, List<String> filters, Repository repository)
    {
        // TODO: the name should be clickable and should point to the raw binary
        // TODO: review how to handle filter properties

        List<PropertyData<?>> cmisPropsList = res.getProperties();
        Map<String, List> cmisPropMap = new HashMap<String, List>();

        for (PropertyData prop : cmisPropsList)
        {
            cmisPropMap.put(prop.getId(), prop.getValues());
        }

        // replace the node name with a wiki-style link
        List listVal = cmisPropMap.get (PropertyIds.NAME);
        String name = (String)listVal.get(0);
        listVal.set(0, String.format ("[%s|%s]", name, Utils.getLink(repository.getId(),
                                                                     (String)res.getPropertyById(PropertyIds.OBJECT_ID).getFirstValue(),
                                                                     Constants.REL_EDITMEDIA,
                                                                     repository.createSession())));

        renderProperty(out, PropertyIds.NAME, cmisPropMap);
        renderProperty(out, PropertyIds.LAST_MODIFICATION_DATE, cmisPropMap);
        renderProperty(out, PropertyIds.CONTENT_STREAM_LENGTH, cmisPropMap);

        for (String filter : filters)
        {
            renderProperty(out, filter, cmisPropMap);
        }
    }

    private void renderProperty(StringBuilder out, String property, Map<String, List> cmisPropMap)
    {
        List valueList = cmisPropMap.get(property);
        if (valueList != null && valueList.size() > 0)
        {
            Iterator i = valueList.iterator();
            while (i.hasNext())
            {
                renderPropertyValue(out, i.next());
                if (i.hasNext())
                {
                    out.append(";");
                }
            }

            out.append("|");
        }
    }

    private void renderPropertyValue(StringBuilder out, Object o)
    {
        if (o instanceof Calendar)
        {
            Calendar cal = (Calendar) o;
            DateFormat df = DateFormat.getDateTimeInstance();
            out.append(df.format(cal.getTime()));
        }
        else
        {
            out.append(o);
        }
    }

    private void renderTitle(StringBuilder out, List<String> filterProperties)
    {
        out.append("||Title||Last Modified||Size||");
        
        if (filterProperties.size() > 0)
        {
            for (String prop : filterProperties)
            {
                out.append(prop).append("||");
            }
        }

        out.append("\n");
    }

    /**
     * Retrieve the list of properties the user wants to show. If they're not overridden within the macro parameters,
     * the global plugin configuration is used as a default
     *
     * @param properties Property list as passed to the macro
     * @return The input properties or the saved onse in case of no 'properties' macro parameter
     */
    private List<String> getProperties(Object properties)
    {
        if (properties instanceof String && !"".equals(properties))
        {
            return Arrays.asList(((String) properties).split(";"));
        }


        logger.debug("Fetching the properties from the global plugin configuration");

        // get the properties enumeration from the plugin conf
        return (List<String>) this.bandanaManager.getValue(new ConfluenceBandanaContext(), ConfigureCMISPluginAction.SEARCH_PROPERTIES_KEY);

    }
}