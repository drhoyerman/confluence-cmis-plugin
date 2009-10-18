package com.sourcesense.confluence.cmis;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;
import org.apache.chemistry.SPI;
import org.apache.chemistry.Type;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import com.sourcesense.confluence.cmis.utils.Utils;

public class AttachmentsAction extends ConfluenceActionSupport {

    private String servername;
    private String folderId;
    private BandanaManager bandanaManager;
    private ConfluenceBandanaContext context = new ConfluenceBandanaContext();
    private List<String> cmisDocumentsInFolder;
    private Map<String, String> folders;
    private Repository repository;

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public String servername() {
        return INPUT;
    }

   
    public String folder() {
        getRepository();
        this.folders = getAllRepositoryFolder();
        return INPUT;
    }
    @SuppressWarnings("unchecked")
    private void getRepository() {
        List<String> repositoryList = ((Map<String, List<String>>) this.bandanaManager.getValue(context, ConfigureCMISPluginAction.CREDENTIALS_KEY))
                                        .get(servername);
        this.repository = Utils.getRepository(repositoryList.get(0), repositoryList.get(1), repositoryList.get(2));
    }

    private Map<String, String> getAllRepositoryFolder() {
        Map<String, String> folders = new HashMap<String, String>();
        Type t = this.repository.getType(BaseType.FOLDER.toString());
        String cmisQuery = "SELECT * FROM " + t.getBaseTypeQueryName();
        Connection conn = this.repository.getConnection(null);
        SPI spi = conn.getSPI();
        Collection<ObjectEntry> results = spi.query(cmisQuery, false, false, false, 1000, 0, new boolean[1]);
        for (ObjectEntry entry : results) {
            CMISObject fold = conn.getObject(entry, null);
            folders.put(fold.getName(), fold.getId());
        }
        return folders;
    }

    public String display() {
        this.cmisDocumentsInFolder = searchDocuments();
        return SUCCESS;
    }

    private List<String> searchDocuments() {
        String id = this.folderId;
        //String id = "workspace://SpacesStore/18574fd2-f42a-4a98-b1c7-416663462d48";
        ObjectEntry folder = null;
        List<ObjectEntry> objects = null;
        if (this.repository != null) {
            folder = Utils.getEntryViaID(this.repository, id, BaseType.FOLDER);
            objects = this.repository.getConnection(null).getSPI().getChildren(folder, BaseType.DOCUMENT, null, false, false, 100, 0, null, new boolean[1]);
        } else {
            List<String> a = new LinkedList<String>();
            a.add("Repo è null");
            return a;
        }

        return renderEntry(objects, this.repository.getConnection(null));
    }

    private List<String> renderEntry(List<ObjectEntry> entries, Connection conn) {
        List<String> result = new LinkedList<String>();
        for (ObjectEntry entry : entries) {
            StringBuilder serializedEntry = new StringBuilder();
            CMISObject doc = conn.getObject(entry, null);
            URI url = doc.getURI(Property.CONTENT_STREAM_URI);
            serializedEntry.append("<a href=\"");
            serializedEntry.append(Utils.rewriteUrl(url, this.servername));
            serializedEntry.append("\" target=\"_blank\">");
            serializedEntry.append(doc.getName());
            result.add(serializedEntry.toString());
        }
        return result;
    }

    public List<String> getOutput() {
        return this.cmisDocumentsInFolder;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public String getServername() {
        return this.servername;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public Map<String, String> getFolders() {
        return folders;
    }

}
