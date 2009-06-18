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



import java.net.URISyntaxException;
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.httpclient.Credentials;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

public class SearchMacro extends BaseCMISMacro {

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, AbderaClient client) throws MacroException {
        String repositoryUsername = (String) params.get("u");
        String repositoryPassword = (String) params.get("p");
        String url = (String) params.get("s");
        
        Credentials credentials = getCredentials(url, repositoryUsername, repositoryPassword);
        if (credentials != null) {
            try {
                client.addCredentials(null, null, null, credentials);
            } catch (URISyntaxException e) {
                throw new MacroException("Error during addCredentials for: " + repositoryUsername, e);
            }
        }

        IRI queryCollectionURL = getQueryCollectionURL(url, client);
        if (queryCollectionURL == null) {
            throw new MacroException("No query collection found in Service Document");
        }
        Document<Feed> doc = doSearch(body, queryCollectionURL.toString(), client);
        return renderFeed(doc.getRoot());
    }

    private String renderFeed(Feed feed) {
        StringBuilder out = new StringBuilder();
        out.append("||Title||Description||\n");
        for (Entry entry : feed.getEntries()) {
            String url = null;
            if (entry.getContentSrc() != null) {
                url = entry.getContentSrc().toString();
            } else if(entry.getLink(CMISConstants.LINK_STREAM) != null) {
                url = entry.getLink(CMISConstants.LINK_STREAM).getHref().toString();
            }
            out.append("|");
            if (url != null) {
                out.append("[");
                out.append(entry.getTitle());
                out.append("|");
                out.append(url);
                out.append("]");
            } else {
                out.append(entry.getTitle());
            }
            out.append("|");
            out.append(entry.getSummary() != null ? entry.getSummary() : " ");
            out.append("|\n");
        }
        return out.toString();
    }

}