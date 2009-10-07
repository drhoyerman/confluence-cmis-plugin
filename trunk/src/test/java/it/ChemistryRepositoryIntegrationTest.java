package it;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Servlet;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.Connection;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.ContentStreamPresence;
import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.atompub.server.servlet.CMISServlet;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.impl.simple.SimplePropertyDefinition;
import org.apache.chemistry.impl.simple.SimpleRepository;
import org.apache.chemistry.impl.simple.SimpleType;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sourcesense.confluence.servlets.CMISNavigationServlet;

/**
 * Tests the AtomPub client with the AtomPub server.
 */
public class ChemistryRepositoryIntegrationTest extends IntegrationTestCmisPlugin {

    public static final String HOST = "127.0.0.1";

    public static final int PORT = 8285;

    public static final String SERVLET_PATH = "/cmis";

    public static final String CMIS_SERVICE = "/repository";

    public Server server;

    public String startServer() throws Exception {
        Repository repository = makeRepo(null);
        server = new Server();
        Connector connector = new SocketConnector();
        connector.setHost(HOST);
        connector.setPort(PORT);
        server.setConnectors(new Connector[] { connector });
        Servlet servlet = new CMISServlet(repository);
        ServletHolder servletHolder = new ServletHolder(servlet);
        Context context = new Context(server, SERVLET_PATH, Context.SESSIONS);
        context.addServlet(servletHolder, "/*");
        server.start();
        String serverUrl = "http://" + HOST + ':' + PORT + SERVLET_PATH + CMIS_SERVICE;
        return serverUrl;
    }

    public static Repository makeRepo(String rootId) throws IOException {
        PropertyDefinition p1 = new SimplePropertyDefinition("title", "def:title", "Title", "", false, PropertyType.STRING, false, null, false, false,
                                        "(no title)", Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);
        PropertyDefinition p2 = new SimplePropertyDefinition("description", "def:description", "Description", "", false, PropertyType.STRING, false, null,
                                        false, false, "", Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);
        PropertyDefinition p3 = new SimplePropertyDefinition("date", "def:date", "Date", "", false, PropertyType.DATETIME, false, null, false, false, null,
                                        Updatability.READ_WRITE, true, true, 0, null, null, -1, null, null);
        SimpleType dt = new SimpleType("doc", "document", "Doc", "My Doc Type", BaseType.DOCUMENT, "", true, true, true, true, true, true,
                                        ContentStreamPresence.ALLOWED, null, null, Arrays.asList(p1, p2, p3));
        SimpleType ft = new SimpleType("fold", "folder", "Fold", "My Folder Type", BaseType.FOLDER, "", true, true, true, true, false, false,
                                        ContentStreamPresence.NOT_ALLOWED, null, null, Arrays.asList(p1, p2));
        SimpleRepository repo = new SimpleRepository("test", Arrays.asList(dt, ft), rootId);
        Connection conn = repo.getConnection(null);
        Folder root = conn.getRootFolder();

        Folder folder1 = root.newFolder("fold");
        folder1.setName("folder 1");
        folder1.setValue("title", "The folder 1 description");
        folder1.setValue("description", "folder 1 title");
        folder1.save();

        Folder folder2 = folder1.newFolder("fold");
        folder2.setName("folder 2");
        folder2.setValue("title", "The folder 2 description");
        folder2.setValue("description", "folder 2 title");
        folder2.save();

        Document doc1 = folder1.newDocument("doc");
        doc1.setName("doc 1");
        doc1.setValue("title", "doc 1 title");
        ContentStream cs1 = new SimpleContentStream("prova".getBytes("UTF-8"), "text/plain", "doc1.txt", null);
        doc1.setContentStream(cs1);
        doc1.setValue("description", "The doc 1 descr");
        doc1.save();

        Document doc2 = folder2.newDocument("doc");
        doc2.setName("doc 2");
        doc2.setValue("title", "doc 2 title");
        ContentStream cs2 = new SimpleContentStream("prova".getBytes("UTF-8"), "text/plain", "doc2.txt", null);
        doc2.setContentStream(cs2);
        doc2.setValue("description", "The doc 2 descr");
        doc2.save();

        Document doc3 = folder2.newDocument("doc");
        doc3.setName("doc 3");
        ContentStream cs3 = new SimpleContentStream("prova".getBytes("UTF-8"), "text/plain", "doc3.txt", null);
        doc3.setContentStream(cs3);
        doc3.save();

        conn.close();
        return repo;
    }

    public void stopServer() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public void setUp() throws Exception {
        try {
            Repository repository = makeRepo(null);
            server = new Server();
            Connector connector = new SocketConnector();
            connector.setHost(HOST);
            connector.setPort(PORT);
            server.setConnectors(new Connector[] { connector });
            Servlet servlet = new CMISServlet(repository);
            ServletHolder servletHolder = new ServletHolder(servlet);
            Context context = new Context(server, SERVLET_PATH, Context.SESSIONS);
            context.addServlet(servletHolder, "/*");
            Servlet navigationServlet = new CMISNavigationServlet();
            ServletHolder servletHolders = new ServletHolder(navigationServlet);
            Context contexts = new Context(server, "/navigation", Context.SESSIONS);
            contexts.addServlet(servletHolders, "/*");
            initTest("http://127.0.0.1:8285/cmis/repository", "admin", "admin");
            server.start();
        } catch (Exception e) {
            stopServer();
            throw e;
        }
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        stopServer();
    }

}
