package com.sourcesense.confluence.cmis.utils;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.spring.container.ContainerManager;
import com.sourcesense.confluence.servlets.CMISProxyServlet;

import java.net.URI;

public class Utils {

/*
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
*/

    public static String getBaseUrl() {
        SettingsManager settingsManager = (SettingsManager) ContainerManager.getComponent("settingsManager");
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        return baseUrl;
    }

    public static String rewriteUrl(URI url, String serverName) {
        if (serverName != null) {
            return Utils.getBaseUrl() + CMISProxyServlet.SERVLET_CMIS_PROXY + url.getPath() + "?servername=" + serverName;
        } else
            return url.toString();
    }
}
