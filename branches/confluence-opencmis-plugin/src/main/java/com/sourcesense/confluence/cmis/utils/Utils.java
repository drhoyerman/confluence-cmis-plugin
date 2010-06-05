package com.sourcesense.confluence.cmis.utils;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import com.sourcesense.confluence.servlets.CMISProxyServlet;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AbstractAtomPubService;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.ObjectServiceImpl;
import org.apache.chemistry.opencmis.commons.impl.Constants;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

  private static Logger logger = Logger.getLogger(Utils.class);

  public static String getLink(Session session, ConfluenceCMISRepository repositoryConfluence, String objectId, boolean rewrite) throws MacroException {
    ObjectServiceImpl objectServices = (ObjectServiceImpl) session.getBinding().getObjectService();
    Class<?>[] parameterTypes = {String.class, String.class, String.class, String.class};
    String res = null;
    try {
      Method loadLink = AbstractAtomPubService.class.getDeclaredMethod("loadLink", parameterTypes);
      loadLink.setAccessible(true);
      res = (String) loadLink.invoke(objectServices, session.getRepositoryInfo().getId(), objectId, Constants.REL_EDITMEDIA, null);
    } catch (Exception e) {
      logger.error(e.getMessage() + "Link retrieval failed");
    }
    if (rewrite) {
      try {
        res = rewriteUrl(res, repositoryConfluence.getName());
      } catch(MalformedURLException e) {
        throw new MacroException(e);
      }
    }
    return res;
  }

  public static String getBaseUrl() {
    SettingsManager settingsManager = (SettingsManager) ContainerManager.getComponent("settingsManager");
    String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
    return baseUrl;
  }

  public static String rewriteUrl(String url, String serverName) throws MalformedURLException {
    URL urlObj = new URL(url);
    url = urlObj.getPath();
    if (serverName != null) {
      return Utils.getBaseUrl() + CMISProxyServlet.SERVLET_CMIS_PROXY + url + "?servername=" + serverName;
    } else
      return url.toString();
  }
}

