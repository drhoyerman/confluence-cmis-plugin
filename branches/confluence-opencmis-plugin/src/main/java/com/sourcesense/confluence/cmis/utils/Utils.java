package com.sourcesense.confluence.cmis.utils;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AbstractAtomPubService;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.ObjectServiceImpl;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

public class Utils {

  private static Logger logger = Logger.getLogger(Utils.class);

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
