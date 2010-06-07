package com.sourcesense.confluence.cmis;

import com.sourcesense.confluence.cmis.utils.ConfluenceCMISRepository;
import com.sourcesense.confluence.cmis.utils.RepositoryStorage;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class TestDocLinkMacro extends AbstractBaseUnitTest
{
    Logger logger = Logger.getLogger(TestDocLinkMacro.class);

    public void testRenderDocumentLink() throws Exception
    {
        Map<String, String> documentProperties = new HashMap<String, String>();

        Property<String> prop = createMockedProperty("Name", "A document name.txt");
        documentProperties.put(prop.getDisplayName(), prop.getValueAsString());

        vc.put("documentProperties", documentProperties);
        vc.put("documentLink", "http://www.sourcesense.com");

        String result = render("templates/cmis/doclink.vm");

        assertEquals("[A document name.txt|http://www.sourcesense.com]", result);
    }

    public void testCMISLinkGeneration()
    {
        RepositoryStorage repoStorage = RepositoryStorage.getInstance(bandanaManager);

        try
        {
            ConfluenceCMISRepository repo = repoStorage.getRepository("test");
            Session session = repo.getRepository().createSession();
            ItemIterable<CmisObject> children = session.getRootFolder().getChildren();
            for (CmisObject obj : children)
            {
                if ("TODO.txt".equals(obj.getName()))
                {
                    Document doc = (Document) obj;
                    logger.debug(doc);
                }
            }
        }
        catch (CmisRuntimeException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
