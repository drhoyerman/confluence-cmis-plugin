package com.sourcesense.confluence.cmis.utils;

import java.net.URI;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.spring.container.ContainerManager;
import com.sourcesense.confluence.servlets.CMISProxyServlet;

public class Utils {


    public static CmisObject getEntryViaID(Session session, String id, BaseTypeId baseType) {
        return  session.getObject(session.createObjectId(id));
    }

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
