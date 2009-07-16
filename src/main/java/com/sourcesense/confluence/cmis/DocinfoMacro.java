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

import java.util.Map;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Repository;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

public class DocinfoMacro extends BaseCMISMacro {

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException {
        String id = (String) params.get("id");
        CMISObject obj = getEntryViaID(repository, id);
        if (obj == null) {
            throw new MacroException("No such object: " + id);
        }
        return renderInfo(obj, repository);
    }

    private String renderInfo(CMISObject cmisObject, Repository repository) {
        StringBuilder out = new StringBuilder();
        out.append("||Property||Value||\n");
        for (String name : cmisObject.getProperties().keySet()) {
            Property property = cmisObject.getProperties().get(name);
            String value = " ";
            if (PropertyType.BOOLEAN.equals(property.getDefinition().getType())) {
                value = Boolean.TRUE.equals(property.getValue()) ? "(/)" : "(x)";
            } else if (PropertyType.URI.equals(property.getDefinition().getType())) {
                value = "[LINK|" + property.getValue() + "]";
            } else if (property.getValue() != null) {
                value = property.getValue().toString();
            }
            out.append("|");
            out.append(name);
            out.append("|");
            out.append(value);
            out.append("|\n");
        }
        return out.toString();
    }

}