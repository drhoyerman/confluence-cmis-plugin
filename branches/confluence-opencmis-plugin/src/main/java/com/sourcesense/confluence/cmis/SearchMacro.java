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

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import org.apache.chemistry.opencmis.client.api.Repository;

import java.util.List;
import java.util.Map;

public class SearchMacro extends BaseCMISMacro {

    private List<String> properties;

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

/*
    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException {
        Connection conn = repository.getConnection(null);
        SPI spi = conn.getSPI();
        Collection<ObjectEntry> res = spi.query(body, false, false, false, 100, 0, new boolean[1]);
        getProperties(params);
        return renderFeed(res, conn);
    }

    private String renderFeed(Collection<ObjectEntry> res, Connection conn) {
        StringBuilder out = new StringBuilder();
        out.append("||Title||Updated||");
        for (String property : this.properties)
            out.append(property + "||");
        out.append("\n");
        for (ObjectEntry oe : res) {
            CMISObject entry = conn.getObject(oe, null);
            out.append("|");
            URI url = entry.getURI(Property.CONTENT_STREAM_URI);
            if (url != null) {
                out.append("[");
                out.append(entry.getName());
                out.append("|");
                out.append(Utils.rewriteUrl(url, serverName));
                out.append("]");
            } else {
                out.append(entry.getName());
            }
            out.append("|");
            Calendar cal = entry.getLastModificationDate();
            if (cal != null) {
                DateFormat df = DateFormat.getDateTimeInstance();
                out.append(df.format(cal.getTime()));
            } else {
                out.append(" ");
            }
            out.append("|");
            for (String property : properties) {
                Property prop = entry.getProperty(property);
                String value = " ";
                if (prop != null)
                    if (PropertyType.BOOLEAN.equals(prop.getDefinition().getType())) {
                        value = Boolean.TRUE.equals(prop.getValue()) ? "(/)" : "(x)";
                    } else if (PropertyType.URI.equals(prop.getDefinition().getType())) {
                        value = "[LINK|" + prop.getValue() + "]";
                    } else if (prop.getValue() != null) {
                        value = prop.getValue().toString();
                    }
                out.append(value + "|");
            }
            out.append("\n");
        }
        return out.toString();
    }

    @SuppressWarnings("unchecked")
    private void getProperties(Map<String, String> params) {
        String properties = params.get("properties");
        if (properties == null) {
            this.properties = (List<String>) this.bandanaManager.getValue(new ConfluenceBandanaContext(), ConfigureCMISPluginAction.SEARCH_PROPERTIES_KEY);
        } else {
            this.properties = Arrays.asList(properties.split(";"));
        }
    }
*/

    @Override
    protected String executeImpl(Map params, String body, RenderContext renderContext, Repository repository)
    {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }
}