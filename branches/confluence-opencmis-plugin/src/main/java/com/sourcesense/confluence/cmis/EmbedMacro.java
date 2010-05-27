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

public class EmbedMacro extends BaseCMISMacro {

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

/*
    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException {
        String id = (String) params.get("id");
        String nf = (String) params.get("nf");
        boolean noformat = nf != null && nf.startsWith("y");
        CMISObject obj = repository.getConnection(null).getObject(Utils.getEntryViaID(repository, id, BaseType.DOCUMENT), null);
        if (obj == null) {
            throw new MacroException("No such object: " + id);
        }
        return renderDocument(obj, repository, noformat);
    }

    private String renderDocument(CMISObject obj, Repository repository, boolean noformat) throws MacroException {

        if (obj instanceof Document) {
            Document doc = (Document) obj;
            StringBuilder out = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(doc.getContentStream().getStream()));
                String line = null;
                if (noformat) {
                    out.append("{noformat}");
                }
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                    out.append("\n");
                }
                if (noformat) {
                    out.append("{noformat}");
                }
            } catch (IOException e) {
                throw new MacroException(e.getMessage(), e);
            }
            return out.toString();
        } else {
            throw new MacroException("Entry is not a document!");
        }
    }
*/

    @Override
    protected String executeImpl(Map params, String body, RenderContext renderContext, Session session)
    {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }
}