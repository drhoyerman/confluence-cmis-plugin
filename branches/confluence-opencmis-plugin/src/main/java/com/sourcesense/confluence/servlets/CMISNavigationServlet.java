package com.sourcesense.confluence.servlets;

import com.atlassian.bandana.BandanaManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CMISNavigationServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private BandanaManager bandanaManager;

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

/*
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String serverName = req.getParameter("servername");
        RepositoryStorage repositoryStorage = RepositoryStorage.getInstance(bandanaManager);
        Repository rep = null;
        try {
            rep = repositoryStorage.getRepository(serverName);
        } catch (NoRepositoryException e) {
            resp.sendError(404);
            return;
        }
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
                result.append("<p class=\"folder\" onclick=\"ajaxFunction('http://" + req.getServerName() + ":" + req.getServerPort() + req.getServletPath() + "/a?id="
                                                + object.getId() + "&servername=" + serverName
                                                + "');\"> "
                                                + object.getName() + " </p>");
            } else if (object.getType().getBaseType().equals(BaseType.DOCUMENT)) {
                String href = "<p class=\"document\"> <a href=\"" + Utils.getBaseUrl();
                href += CMISProxyServlet.SERVLET_CMIS_PROXY;
                href += object.getURI(Property.CONTENT_STREAM_URI).getPath() + "?servername=" + serverName + "\" target=\"_blank\"> </p>";
                result.append(href + object.getName()); 
            }
        }
        resp.getWriter().write(result.toString());
    }
*/

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

}
