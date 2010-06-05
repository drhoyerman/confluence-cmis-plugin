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
import com.sourcesense.confluence.cmis.utils.Utils;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.impl.Constants;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DocinfoMacro extends BaseCMISMacro {


  @Override
  protected String executeImpl(Map params, String body, RenderContext renderContext, Repository repository) throws MacroException {
    Session session = repository.createSession();
    String documentId = (String) params.get(BaseCMISMacro.PARAM_ID);

    ObjectId objectId = session.createObjectId(documentId);
    CmisObject cmisObject = (Document) session.getObject(objectId);

    if (cmisObject == null) {
      throw new MacroException("Cannot find any document with the following ID: " + documentId);
    }

    String title = cmisObject.getProperty(PropertyIds.NAME).getValueAsString();
    String link = Utils.getLink(session.getRepositoryInfo().getId(), documentId, Constants.REL_EDITMEDIA, session);

    StringBuilder sb = new StringBuilder(String.format("*Details of %s*\n", String.format("[%s|%s]", title, link)));

    return renderDocumentInfo(sb, cmisObject);
  }

  protected String renderDocumentInfo(StringBuilder sb, CmisObject cmisObject) {
    List<Property<?>> properties = cmisObject.getProperties();

    sb.append("||Property||Value||\n");
    for (Property<?> prop : properties) {
      String stringValue = prop.getValueAsString();
      if (PropertyType.DATETIME.equals(prop.getType())) {
        Calendar cal = (Calendar) prop.getFirstValue();
        DateFormat df = DateFormat.getDateTimeInstance();
        stringValue = df.format(cal.getTime());
      }
      sb.append(String.format("|%s|%s|\n", prop.getDisplayName(), stringValue));
    }

    return sb.toString();
  }
}