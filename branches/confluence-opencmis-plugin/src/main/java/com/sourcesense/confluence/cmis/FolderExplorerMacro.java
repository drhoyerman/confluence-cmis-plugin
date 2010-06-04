package com.sourcesense.confluence.cmis;

import java.math.BigInteger;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

public class FolderExplorerMacro extends BaseCMISMacro {
  
  private static final String PARAM_FOLDER_ID = "id";
  private static final String PARAM_RESULTS_NUMBER = "rn";

  @Override
  protected String executeImpl(Map params, String body, RenderContext renderContext,
          Repository repository) throws MacroException {
    Session session = repository.createSession();
    String folderId = (String) params.get(PARAM_FOLDER_ID);
    int number = Integer.parseInt((String) params.get(PARAM_RESULTS_NUMBER));
    
    ObjectInFolderList list =session.getBinding().getNavigationService().getChildren(repository.getId(), folderId, null, null, false, null, null, null, BigInteger.valueOf(number), BigInteger.ZERO, null);
    CmisObject o =session.getObject(session.createObjectId(list.getObjects().get(0).getObject().getId()));
    

    
    return null;
  }

  public RenderMode getBodyRenderMode() {
    return null;
  }

  public boolean hasBody() {
    return false;
  }

}
