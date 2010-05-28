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
import com.atlassian.renderer.v2.macro.MacroException;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;

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

    @Override
    protected String executeImpl(Map params, String body, RenderContext renderContext, Repository repository) throws MacroException
    {
        Session session = repository.createSession();
        String documentId = (String)params.get(PARAM_DOCUMENT_ID);

        ObjectId objectId = session.createObjectId(documentId);
        CmisObject cmisObject = (Document)session.getObject(objectId);

        if (cmisObject == null)
        {
            throw new MacroException("Cannot find any document with the following ID: " + documentId);
        }

        return renderDocumentInfo(cmisObject);
    }

    protected String renderDocumentInfo(CmisObject cmisObject)
    {
        List<Property<?>> properties = cmisObject.getProperties();
        Property<String> title = cmisObject.getProperty(PropertyIds.NAME);

        StringBuilder sb = new StringBuilder (String.format ("*Details of %s*\n", title.getValueAsString()));

        sb.append ("||Property||Value||\n");
        for (Property<?> prop : properties)
        {
            // TODO: handle different propertyTypes
            sb.append (String.format ("|%s|%s|\n", prop.getDisplayName(), prop.getValueAsString()));
        }

        return sb.toString ();
    }
}