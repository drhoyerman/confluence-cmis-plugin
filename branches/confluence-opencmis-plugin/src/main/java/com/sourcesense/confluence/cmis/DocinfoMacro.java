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
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.PropertyId;

import java.util.List;
import java.util.Map;

public class DocinfoMacro extends BaseCMISMacro {

    public static final String PARAM_DOCUMENT_ID = "id";

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

/*
    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException {
        String id = (String) params.get("id");
        Connection conn = repository.getConnection(null);
        CMISObject obj = conn.getObject(Utils.getEntryViaID(repository, id, BaseType.DOCUMENT), null);
        if (obj == null) {
            throw new MacroException("No such object: " + id);
        }
        return renderInfo(obj, repository);
    }

    private String renderInfo(CMISObject cmisObject, Repository repository) {
        StringBuilder out = new StringBuilder();
        out.append("||Property||Value||\n");
        for (String name : cmisObject.getProperties().keySet()) { //it return all properties name if the properties don't exists return null
            Property property = cmisObject.getProperties().get(name);
            String value = " ";
            if (property != null) {
                if (PropertyType.BOOLEAN.equals(property.getDefinition().getType())) {
                    value = Boolean.TRUE.equals(property.getValue()) ? "(/)" : "(x)";
                } else if (PropertyType.URI.equals(property.getDefinition().getType())) {
                    value = "[LINK|" + property.getValue() + "]";
                } else if (property.getValue() != null) {
                    value = property.getValue().toString();
                }
            }
            out.append("|");
            out.append(name);
            out.append("|");
            out.append(value);
            out.append("|\n");
        }
        return out.toString();
    }
*/

    @Override
    protected String executeImpl(Map params, String body, RenderContext renderContext, Session session)
    {
        String documentId = (String)params.get(PARAM_DOCUMENT_ID);
        ObjectId objectId = session.createObjectId(documentId);

        Document document = (Document)session.getObject(objectId);

        return renderDocument (document);  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected String renderDocument(Document document)
    {
        List<Property<?>> properties = document.getProperties();
        Property<String> title = document.getProperty(PropertyIds.NAME);

        StringBuffer sb = new StringBuffer (String.format ("*Details of %s*\n", title.getValueAsString()));

        sb.append ("||Property||Value||\n");
        for (Property<?> prop : properties)
        {
            // TODO: handle different propertyTypes
            sb.append (String.format ("|%s|%s|\n", prop.getDisplayName(), prop.getValueAsString()));
        }

        return sb.toString ();
    }
}