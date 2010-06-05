/*
 * Copyright 2010 Sourcesense <http://www.sourcesense.com>
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

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FolderExplorerMacro extends BaseCMISMacro {

  public static final String PARAM_FOLDER_ID = "id";

  public static final String PARAM_RESULTS_NUMBER = "maxResults";

  @Override
  protected String executeImpl(Map params, String body, RenderContext renderContext,
                               Repository repository) throws MacroException {
    Session session = repository.createSession();
    String folderId = (String) params.get(PARAM_FOLDER_ID);
    int resultsNumber = Integer.parseInt((String) params.get(PARAM_RESULTS_NUMBER));
    Folder folder = (Folder) session.getObject(session.createObjectId(folderId));
    ItemIterable<CmisObject> children = folder.getChildren();
    List<CmisObject> filteredResults = new LinkedList<CmisObject>();
    for (CmisObject cmisObject : children) {
      if (filteredResults.size() < resultsNumber) {
        if (cmisObject.getBaseType().getId().equals(BaseTypeId.CMIS_DOCUMENT.value())) {
          filteredResults.add(cmisObject);
        }
      } else {
        break;
      }

    }
    return renderResults(filteredResults);
  }

  private String renderResults(List<CmisObject> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  public RenderMode getBodyRenderMode() {
    return null;
  }

  public boolean hasBody() {
    return false;
  }

}
