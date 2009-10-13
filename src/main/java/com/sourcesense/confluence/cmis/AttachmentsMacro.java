package com.sourcesense.confluence.cmis;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

public class AttachmentsMacro extends BaseCMISMacro {

    @Override
    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) throws MacroException {
        String id = params.get("id");
        ObjectEntry folder = getEntryViaID(repository, id, BaseType.FOLDER);
        boolean[] hasMoreItem = new boolean[1];
        List<ObjectEntry> objects = repository.getConnection(null).getSPI().getChildren(folder, BaseType.DOCUMENT, null, false, false, 100, 0, null,
                                        hasMoreItem);

        return renderEntry(objects, repository.getConnection(null));
    }

    private String renderEntry(List<ObjectEntry> entries, Connection conn) {
        StringBuilder out = new StringBuilder();
        for (ObjectEntry entry : entries) {
            CMISObject doc = conn.getObject(entry, null);
            URI url = doc.getURI(Property.CONTENT_STREAM_URI); // XXX Should there be a constant definition in CMIS class for this?
            out.append("[");
            out.append(doc.getName());
            out.append("|");
            out.append(rewriteUrl(url));
            out.append("]");
        }
        return out.toString();
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

    public boolean hasBody() {
        return false;
    }

    public boolean isInline() {
        return false;
    }

}
