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



import java.util.Collection;
import java.util.Map;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.Repository;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

public class SearchMacro extends BaseChemistryCMISMacro {

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException {
        Connection conn = repository.getConnection(null);
        Collection<CMISObject> res = conn.query(body, false);
        return renderFeed(res);
    }

    private String renderFeed(Collection<CMISObject> res) {
        StringBuilder out = new StringBuilder();
        out.append("||Title||Description||\n");
        for (CMISObject entry : res) {
            String url = null;
            /*
            if (entry.getContentSrc() != null) {
                url = entry.getContentSrc().toString();
            } else if(entry.getLink(CMISConstants.LINK_STREAM) != null) {
                url = entry.getLink(CMISConstants.LINK_STREAM).getHref().toString();
            }
            */
            out.append("|");
            if (url != null) {
                out.append("[");
                out.append(entry.getName());
                out.append("|");
                out.append(url);
                out.append("]");
            } else {
                out.append(entry.getName());
            }
            out.append("|");
            // out.append(entry.getSummary() != null ? entry.getSummary() : " ");
            out.append("|\n");
        }
        return out.toString();
    }

}