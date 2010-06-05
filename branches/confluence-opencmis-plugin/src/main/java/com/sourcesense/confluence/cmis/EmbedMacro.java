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
import com.atlassian.renderer.v2.macro.MacroException;
import org.apache.chemistry.opencmis.client.api.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class EmbedMacro extends BaseCMISMacro {

  @Override
  protected String executeImpl(Map params, String body, RenderContext renderContext, Repository repository) throws MacroException {
    String documentId = (String) params.get(BaseCMISMacro.PARAM_ID);
    String noformat = (String) params.get(BaseCMISMacro.PARAM_NOFORMAT);

    boolean isNoFormat = (noformat == null) ? false : (noformat.startsWith("y") ? true : false);

    Session session = repository.createSession();

    ObjectId objectId = session.createObjectId(documentId);
    CmisObject cmisObject = session.getObject(objectId);

    String result = "";

    if (cmisObject != null && cmisObject instanceof Document) {
      result = renderDocument((Document) cmisObject, isNoFormat);
    } else {
      throw new MacroException("Object with id " + documentId + " is not a Document or is null");
    }

    return result;
  }

  // TODO: is this really going to work with mime types other than text/plain?
  private String renderDocument(Document document, boolean noformat) throws MacroException {
    StringBuilder out = new StringBuilder();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(document.getContentStream().getStream()));
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
      throw new MacroException(e);
    }
    return out.toString();

  }
}