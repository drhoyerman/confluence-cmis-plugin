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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;

public abstract class BaseCMISMacro extends BaseMacro {

    private BandanaManager bandanaManager;

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @SuppressWarnings("unchecked")
    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        
        AbderaClient client;
        
        // Work around Abdera trying to be smart with class loading
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            
            Abdera abdera = new Abdera();
            client = new AbderaClient(abdera);
            
            if (abdera.getParser() == null) {
                return "NO PARSER!";
            }
    
            return doExecute(params, body, renderContext, client);
        } finally {
            // Restore original classloader
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    protected abstract String doExecute(Map<String, String> params, String body, RenderContext renderContext, AbderaClient client) throws MacroException;

    protected Document<Feed> doSearch(String cmisQuery, String queryCollectionURL, AbderaClient client) throws MacroException {
        String body = "<cmis:query xmlns:cmis='" + CMISConstants.CMIS_NS_URI + "'>" + 
                "<cmis:statement><![CDATA[" + cmisQuery + "]]></cmis:statement>" +
                "<cmis:searchAllVersions>false</cmis:searchAllVersions>" + 
                // "<cmis:pageSize>10</cmis:pageSize>" +
                "<cmis:skipCount>0</cmis:skipCount>" + 
                "</cmis:query>";
        byte[] bytes = body.getBytes();
        RequestEntity entity = new ByteArrayRequestEntity(bytes);
        RequestOptions options = client.getDefaultRequestOptions();
        options.setContentType("application/cmisquery+xml");
        ClientResponse clientResponse = client.post(queryCollectionURL, entity, options);
        if (clientResponse.getStatus() == HttpServletResponse.SC_OK) {
            return clientResponse.getDocument();
        } else {
            throw new MacroException(clientResponse.getStatus() + " " + clientResponse.getStatusText());
        }
    }

    protected IRI getQueryCollectionURL(String serviceURL, AbderaClient client) throws MacroException {
        ClientResponse clientResponse = client.get(serviceURL);
        if (clientResponse.getStatus() == HttpServletResponse.SC_OK) {
            Document<Service> doc = clientResponse.getDocument();
            Collection coll = doc.getRoot().getCollectionThatAccepts("application/cmisquery+xml");
            if (coll == null) {
                return null;
            }
            return coll.getHref();
        } else {
            throw new MacroException(clientResponse.getStatus() + " " + clientResponse.getStatusText());
        }
    }

    protected Entry getEntryViaID(AbderaClient client, String url, String id) throws MacroException {
        IRI queryCollectionURL = getQueryCollectionURL(url, client);
        if (queryCollectionURL == null) {
            throw new MacroException("No query collection found in Service Document");
        }
        String cmisQuery = "SELECT * FROM DOCUMENT WHERE ObjectId = '" + id + "'";
        Document<Feed> doc = doSearch(cmisQuery, queryCollectionURL.toString(), client);
        if (doc.getRoot().getEntries().isEmpty()) {
            return null;
        }
        Entry entry = doc.getRoot().getEntries().get(0);
        return entry;
    }
    
    @SuppressWarnings("unchecked")
    protected Credentials getCredentials(String url, String username, String password) {
        if (username != null && password != null) {
            return new UsernamePasswordCredentials(username , password);
        }
        Map<String, List<String>> credsMap = (Map<String, List<String>>) this.bandanaManager.getValue(new ConfluenceBandanaContext(), 
                                        ConfigureCMISPluginAction.CREDENTIALS_KEY);
        if (credsMap == null) {
            return null;
        }
        for (String realm : credsMap.keySet()) {
            if (url.startsWith(realm)) {
                List<String> up = credsMap.get(realm);
                return new UsernamePasswordCredentials(up.get(0), up.get(1));
            }
        }
        return null;
    }
}