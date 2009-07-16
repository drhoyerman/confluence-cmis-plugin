/*
 * Copyright 2009 Sourcesense <http://www.sourcesense.com>
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



import java.net.URI;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

public class SearchMacro extends BaseCMISMacro {

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException {
        Connection conn = repository.getConnection(null);
        SPI spi = conn.getSPI();
        Collection<ObjectEntry> res = spi.query(body, false, false, false, 100, 0, new boolean[1]);
        return renderFeed(res, conn);
    }

    private String renderFeed(Collection<ObjectEntry> res, Connection conn) {
        StringBuilder out = new StringBuilder();
        out.append("||Title||Updated||\n");
        for (ObjectEntry oe : res) {
            CMISObject entry = conn.getObject(oe, null);
            out.append("|");
            URI url = entry.getURI("ContentStreamUri"); // XXX Should there be a constant definition in CMIS class for this?
            if (url != null) {
                out.append("[");
                out.append(entry.getName());
                out.append("|");
                out.append(url);
                out.append("]");
            } else {
                out.append(entry.getName());
            }
            out.append("|");
            Calendar cal = entry.getDateTime("LastModificationDate");
            if (cal != null) {
                DateFormat df = DateFormat.getDateTimeInstance();
                out.append(df.format(cal.getTime()));
            } else {
                out.append(" ");
            }
            out.append("|\n");
        }
        return out.toString();
    }

}