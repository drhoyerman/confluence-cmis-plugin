package com.sourcesense.confluence.cmis;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttachmentsAction extends ConfluenceActionSupport {

    private String servername;
    private String folderId;
    private BandanaManager bandanaManager;
    private List<String> cmisDocumentsInFolder;
    private Map<String, String> folders;
    private Set<String> repositoryNames;

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public String servername() {
        this.repositoryNames = RepositoryStorage.getInstance(bandanaManager).getRepositoryNames();
        return INPUT;
    }

/*
    public String folder() {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
            try {
                this.folders = getAllRepositoryFolder();
            } catch (NoRepositoryException e) {
                LOG.error(e.getMessage());
                return ERROR;
            }
        } finally {
            // Restore original classloader
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
        return INPUT;
    }
*/
/*

    private Repository getRepository() throws NoRepositoryException {
        return RepositoryStorage.getInstance(bandanaManager).getRepository(servername);
    }

    private Map<String, String> getAllRepositoryFolder() throws NoRepositoryException {
        Map<String, String> folders = new HashMap<String, String>();
        Repository repository = getRepository();
        Type t = repository.getType(BaseType.FOLDER.toString());
        String cmisQuery = "SELECT * FROM " + t.getBaseTypeQueryName();
        Connection conn = repository.getConnection(null);
        SPI spi = conn.getSPI();
        Collection<ObjectEntry> results = spi.query(cmisQuery, false, false, false, 1000, 0, new boolean[1]);
        for (ObjectEntry entry : results) {
            CMISObject fold = conn.getObject(entry, null);
            folders.put(fold.getName(), fold.getId());
        }
        return folders;
    }

    public String display() {
        try {
            this.cmisDocumentsInFolder = searchDocuments();
        } catch (NoRepositoryException e) {
            LOG.error(e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    private List<String> searchDocuments() throws NoRepositoryException {
        String id = this.folderId;
        //String id = "workspace://SpacesStore/18574fd2-f42a-4a98-b1c7-416663462d48";
        ObjectEntry folder = null;
        Repository repository = getRepository();
        List<ObjectEntry> objects = null;
        folder = Utils.getEntryViaID(repository, id, BaseType.FOLDER);
        objects = repository.getConnection(null).getSPI().getChildren(folder, BaseType.DOCUMENT, null, false, false, 100, 0, null, new boolean[1]);

        return renderEntry(objects, repository.getConnection(null));
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
*/

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
        return this.folders;
    }

    public Set<String> getRepositorynames() {
        return this.repositoryNames;
    }

}
