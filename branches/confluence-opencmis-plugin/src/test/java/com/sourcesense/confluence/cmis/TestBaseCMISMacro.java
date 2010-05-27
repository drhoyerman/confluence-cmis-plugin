package com.sourcesense.confluence.cmis;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import com.sourcesense.confluence.cmis.exception.NoRepositoryException;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import junit.framework.TestCase;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class TestBaseCMISMacro extends TestCase
{
    Logger logger = Logger.getLogger(TestBaseCMISMacro.class);

    public void testRepositoryConnection() throws Exception
    {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("s", "http://localhost:8081/alfresco/service/cmis");
//        parameters.put("id", "alfresco");
        parameters.put("n", "alfresco");
        parameters.put("u", "admin");
        parameters.put("p", "admin");

        BaseCMISMacro baseMacro = new BaseCMISMacro()
        {

            public boolean hasBody()
            {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public RenderMode getBodyRenderMode()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            protected String executeImpl(Map params, String body, RenderContext renderContext, Session session)
            {
                return "OK";
            }
        };

        baseMacro.setBandanaManager(new MockBandanaManager());

        String result = null;

        try
        {
            result = baseMacro.execute(parameters, null, null);
        }
        catch (MacroException me)
        {
            logger.error(me);
            fail(me.getMessage());
        }

        assertNotNull(result);
        assertFalse("".equals(result));

        logger.debug(result);
    }

    public void testRepositoryEnumeration()
    {

        RepositoryStorage repoStorage = RepositoryStorage.getInstance(new MockBandanaManager());

        Set<String> repos = repoStorage.getRepositoryNames();

        assertNotNull (repos);
        assertTrue (!repos.isEmpty());

        for (String repo : repos)
        {
            try
            {
                Repository repoDesc = repoStorage.getRepository(repo);

                logger.debug ("name: " + repoDesc.getName());
                logger.debug ("id: " + repoDesc.getId ());
                logger.debug ("productName : " + repoDesc.getProductName());
                logger.debug ("cmisVersionSupported: " + repoDesc.getCmisVersionSupported());
                logger.debug ("description: " + repoDesc.getDescription());
            }
            catch (NoRepositoryException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                fail (e.getMessage());
            }
        }

    }

    class MockBandanaManager implements BandanaManager
    {

        public void init()
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void setValue(BandanaContext bandanaContext, String s, Object o)
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        // used in RepositoryStorage

        public Object getValue(BandanaContext bandanaContext, String s)
        {
            Map<String, List<String>> repoConfigs = new WeakHashMap<String, List<String>>();
            List<String> repoConfig = new ArrayList<String>();

            repoConfig.add("http://localhost:8081/alfresco/service/cmis");
            repoConfig.add("admin");
            repoConfig.add("admin");

            repoConfigs.put("alfresco", repoConfig);

            return repoConfigs;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getValue(BandanaContext bandanaContext, String s, boolean b)
        {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String exportValues(BandanaContext bandanaContext)
        {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void importValues(BandanaContext bandanaContext, String s)
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
