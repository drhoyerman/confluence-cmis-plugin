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

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.Credentials;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

public class EmbedMacro extends BaseCMISMacro {

    public boolean isInline() {
        return true;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public String doExecute(Map params, String body, RenderContext renderContext, AbderaClient client) throws MacroException {
        String repositoryUsername = (String) params.get("u");
        String repositoryPassword = (String) params.get("p");
        String url = (String) params.get("s");
        String id = (String) params.get("id");
        String nf = (String) params.get("nf");
        boolean noformat = nf != null && nf.startsWith("y"); 
        
        
        Credentials credentials = getCredentials(url, repositoryUsername, repositoryPassword);
        if (credentials != null) {
            try {
                client.addCredentials(null, null, null, credentials);
            } catch (URISyntaxException e) {
                throw new MacroException("Error during addCredentials for: " + repositoryUsername, e);
            }
        }
        
        if (id != null) { // Search by ObjectId
            Entry entry = getEntryViaID(client, url, id);
            if (entry != null) {
                return renderDocument(entry, client, noformat);
            } else {
                throw new MacroException("No such document");
            }
        } else { // Fetch entry via its URI
            ClientResponse clientResponse = client.get(url);
            if (clientResponse.getStatus() == HttpServletResponse.SC_OK) {
                Document<Entry> doc = clientResponse.getDocument();
                return renderDocument(doc.getRoot(), client, noformat);
            } else {
                throw new MacroException(clientResponse.getStatus() + " " + clientResponse.getStatusText());
            }
        }
    }

    private String renderDocument(Entry entry, AbderaClient client, boolean noformat) throws MacroException {
        StringBuilder out = new StringBuilder();
        String url = null;
        if (entry.getContentSrc() != null) {
            url = entry.getContentSrc().toString();
        } else if(entry.getLink(CMISConstants.LINK_STREAM) != null) {
            url = entry.getLink(CMISConstants.LINK_STREAM).getHref().toString();
        } else {
            throw new MacroException("Document has no content!");
        }
        ClientResponse clientResponse = client.get(url);
        try {
            BufferedReader reader = new BufferedReader(clientResponse.getReader());
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
        } finally {
            if (clientResponse != null) {
                clientResponse.release();
            }
        }
        return out.toString();
    }

}