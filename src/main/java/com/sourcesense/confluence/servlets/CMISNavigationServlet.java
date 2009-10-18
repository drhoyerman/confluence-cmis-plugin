package com.sourcesense.confluence.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CMISObject;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;
import org.apache.chemistry.Repository;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.connector.APPContentManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.sourcesense.confluence.cmis.configuration.ConfigureCMISPluginAction;
import com.sourcesense.confluence.cmis.utils.Utils;

public class CMISNavigationServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private BandanaManager bandanaManager;

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String serverName = req.getParameter("servername");
        ContentManager cm = new APPContentManager(getServerUrl(req));
        UsernamePasswordCredentials credentials = getCredentials(serverName);
        cm.login(credentials.getUserName(), credentials.getPassword());
        Repository rep = cm.getDefaultRepository();
        ObjectEntry sourceEntry = Utils.getEntryViaID(rep, req.getParameter("id"), BaseType.FOLDER);
        boolean[] hasMoreItem = new boolean[1];
        List<ObjectEntry> objects = rep.getConnection(null).getSPI().getChildren(sourceEntry, null, null, false, false, 100, 0, null, hasMoreItem);
        StringBuilder result = new StringBuilder();
        CMISObject parent = rep.getConnection(null).getObject(sourceEntry, null).getParent();
        String oldId = null;
        if (parent != null) {
            oldId = parent.getId();
        }
        if (oldId != null) {
            result.append("<p onclick=\"ajaxFunction('http://" + req.getServerName() + ":" + req.getServerPort() + req.getServletPath() + "/a?id=" + oldId
                                            + "&servername=" + serverName + "');\">" + "Back" + "</p>");
        }
        for (ObjectEntry entry : objects) {
            CMISObject object = rep.getConnection(null).getObject(entry, null);
            if (object.getType().getBaseType().equals(BaseType.FOLDER)) {
                result.append("<p onclick=\"ajaxFunction('http://" + req.getServerName() + ":" + req.getServerPort() + req.getServletPath() + "/a?id="
                                                + object.getId() + "&servername=" + serverName
                                                + "');\"> <img src=\"http://icons.iconarchive.com/icons/mart/glaze/folder-yellow-open-icon.jpg\" \\>"
                                                + object.getName() + "</p>");
            } else if (object.getType().getBaseType().equals(BaseType.DOCUMENT)) {
                String href = "<a href=\"" + Utils.getBaseUrl();
                href += CMISProxyServlet.SERVLET_CMIS_PROXY;
                href += object.getURI(Property.CONTENT_STREAM_URI).getPath() + "?servername=" + serverName + "\" target=\"_blank\">";
                result.append(href + object.getName());
            }
        }
        resp.getWriter().write(result.toString());
    }

    private UsernamePasswordCredentials getCredentials(String servername) {
        List<String> up = getConfigurationList(servername);
        if (up != null) {
            return new UsernamePasswordCredentials(up.get(1), up.get(2));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getConfigurationList(String servername) {
        Map<String, List<String>> credsMap = (Map<String, List<String>>) this.bandanaManager.getValue(new ConfluenceBandanaContext(),
                                        ConfigureCMISPluginAction.CREDENTIALS_KEY);
        if (credsMap == null || servername == null) {
            return null;
        }
        return credsMap.get(servername);
    }

    private String getServerUrl(HttpServletRequest req) {
        String serverName = req.getParameter("servername");
        if (serverName != null) {
            List<String> up = getConfigurationList(serverName);
            if (up != null) {
                return up.get(0);
            }
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

}
