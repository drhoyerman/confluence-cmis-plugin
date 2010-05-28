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

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AbstractAtomPubService;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.ObjectServiceImpl;
import org.apache.chemistry.opencmis.commons.impl.Constants;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

public class DoclinkMacro extends BaseCMISMacro
{
    public static final String PARAM_DOCUMENT_ID = "id";

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
    protected String executeImpl(Map params, String body, RenderContext renderContext, Repository repository) throws MacroException
    {
        Session session = repository.createSession();

        String documentId = (String)params.get(PARAM_DOCUMENT_ID);
        ObjectId objectId = session.createObjectId(documentId);

        Document document = (Document)session.getObject(objectId);

        if (document == null)
        {
            throw new MacroException("Cannot find any document with the following ID: " + documentId);
        }
        String link = getLink(session.getRepositoryInfo().getId(),documentId, Constants.REL_EDITMEDIA, session);

        return renderDocumentLink(document, link);
    }

    protected String renderDocumentLink (Document document, String link )
    {
        StringBuilder out = new StringBuilder();

        out.append("[");
        out.append(document.getName());
        out.append("|");
        out.append(link);
        out.append("]");

        return out.toString();
    }

    private String getLink(String repositoryId,String objectId,String rel,Session session) {
      ObjectServiceImpl objectServices = (ObjectServiceImpl) session.getBinding().getObjectService();
      Class<?> [] parameterTypes = {String.class,String.class,String.class,String.class} ;
      String res = null;
      try {
        Method loadLink = AbstractAtomPubService.class.getDeclaredMethod("loadLink", parameterTypes );
        loadLink.setAccessible(true);
        res = (String) loadLink.invoke(objectServices, repositoryId,objectId,rel,null);
      } catch (Exception e) {
        logger.error(e.getMessage() +"Link retrieval failed");
      }
    return res;
    }

}