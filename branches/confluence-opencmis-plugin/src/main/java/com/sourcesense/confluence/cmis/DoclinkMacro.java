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
import org.apache.chemistry.opencmis.client.api.Session;

import java.util.Map;

public class DoclinkMacro extends BaseCMISMacro {

    public boolean isInline() {
        return true;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

/*
    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Session session) throws MacroException {
        String id = (String) params.get("id");
        CMISObject obj = repository.getConnection(null).getObject(Utils.getEntryViaID(repository, id, BaseType.DOCUMENT), null);
        if (obj == null) {
            throw new MacroException("No such object: " + id);
        }
        return renderEntry(obj);

    }
*/

/*
    private String renderEntry(CMISObject entry) {
        StringBuilder out = new StringBuilder();
        URI url = entry.getURI(Property.CONTENT_STREAM_URI); // XXX Should there be a constant definition in CMIS class for this?
        out.append("[");
        out.append(entry.getName());
        out.append("|");
        out.append(Utils.rewriteUrl(url, serverName));
        out.append("]");
        return out.toString();
    }
*/

    @Override
    protected String executeImpl(Map params, String body, RenderContext renderContext, Session session)
    {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }
}