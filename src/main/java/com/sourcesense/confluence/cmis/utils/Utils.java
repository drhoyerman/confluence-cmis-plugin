package com.sourcesense.confluence.cmis.utils;

import java.net.URI;
import java.util.Collection;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.connector.APPContentManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.spring.container.ContainerManager;
import com.sourcesense.confluence.servlets.CMISProxyServlet;

public class Utils {

    public static ObjectEntry getEntryViaID(Repository repository, String id, BaseType type) {
        Type t = repository.getType(type.toString());
        String cmisQuery = "SELECT * FROM " + t.getBaseTypeQueryName() + " WHERE ObjectId = '" + id + "'"; //cmis:document is the actual (0.62Spec) common query name
        Connection conn = repository.getConnection(null);
        SPI spi = conn.getSPI();
        Collection<ObjectEntry> res = spi.query(cmisQuery, false, false, false, 1, 0, new boolean[1]);
        for (ObjectEntry entry : res) {
            return entry;
        }
        return null;
    }

    public static Repository getRepository(String url, String username, String password) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        ContentManager cm = new APPContentManager(url);
        cm.login(credentials.getUserName(), credentials.getPassword());
        return cm.getDefaultRepository();
    }

    public static String getBaseUrl() {
        SettingsManager settingsManager = (SettingsManager) ContainerManager.getComponent("settingsManager");
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        return baseUrl;
    }

    public static String rewriteUrl(URI url, String serverName) {
        return Utils.getBaseUrl() + CMISProxyServlet.SERVLET_CMIS_PROXY + url.getPath() + "?servername=" + serverName;
    }
}
