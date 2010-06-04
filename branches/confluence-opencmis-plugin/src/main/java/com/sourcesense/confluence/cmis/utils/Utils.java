package com.sourcesense.confluence.cmis.utils;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.spring.container.ContainerManager;
import com.sourcesense.confluence.servlets.CMISProxyServlet;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AbstractAtomPubService;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.ObjectServiceImpl;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.net.URI;

public class Utils {

  private static Logger logger = Logger.getLogger("com.sourcesense.confluence.cmis.utils");

  public static CmisObject getEntryViaID(Session session, String id, BaseTypeId baseType) {
    return session.getObject(session.createObjectId(id));
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

  public static String getLink(String repositoryId, String objectId, String rel, Session session) {
    ObjectServiceImpl objectServices = (ObjectServiceImpl) session.getBinding().getObjectService();
    Class<?>[] parameterTypes = {String.class, String.class, String.class, String.class};
    String res = null;
    try {
      Method loadLink = AbstractAtomPubService.class.getDeclaredMethod("loadLink", parameterTypes);
      loadLink.setAccessible(true);
      res = (String) loadLink.invoke(objectServices, repositoryId, objectId, rel, null);
    } catch (Exception e) {
      logger.error(e.getMessage() + "Link retrieval failed");
    }
    return res;
  }
}
