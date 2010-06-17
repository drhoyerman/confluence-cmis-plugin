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
package com.sourcesense.confluence.cmis.macros;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.utils.ConfluenceCMISRepository;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FolderExplorerMacro extends BaseCMISMacro
{


    @Override
    protected String executeImpl(Map params, String body, RenderContext renderContext,
                                 ConfluenceCMISRepository confluenceCmisRepository) throws MacroException
    {
        Session session = confluenceCmisRepository.getSession();
        String folderId = (String) params.get(PARAM_ID);
//        boolean useProxy = (Boolean) params.get(BaseCMISMacro.PARAM_USEPROXY);
        int resultsNumber = (Integer)params.get(PARAM_RESULTS_NUMBER);

        if (resultsNumber < 1)
        {
            resultsNumber = DEFAULT_RESULTS_NUMBER;
        }

        Folder folder = (Folder) session.getObject(session.createObjectId(folderId));
        ItemIterable<CmisObject> children = folder.getChildren();
        List<CmisObject> filteredResults = new LinkedList<CmisObject>();
        for (CmisObject cmisObject : children)
        {
            if (filteredResults.size() < resultsNumber)
            {
                if (cmisObject.getBaseType().getId().equals(BaseTypeId.CMIS_DOCUMENT.value()))
                {
                    filteredResults.add(cmisObject);
                }
            }
            else
            {
                break;
            }
        }

        renderContext.addParam(VM_CMIS_OBJECT, folder);
        renderContext.addParam(VM_CMIS_OBJECT_LIST, filteredResults);        

        return render(getTemplate(), renderContext);
    }

    @Override
    protected String getTemplate ()
    {
        return "templates/cmis/folderbrowser.vm";
    }
}
