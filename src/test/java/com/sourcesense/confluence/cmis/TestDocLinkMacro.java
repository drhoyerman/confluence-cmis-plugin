package com.sourcesense.confluence.cmis;

import com.atlassian.renderer.v2.macro.MacroException;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.log4j.Logger;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class TestDocLinkMacro extends AbstractBaseUnitTest
{
    Logger logger = Logger.getLogger(TestDocLinkMacro.class);

    public void testRenderDocumentLink() throws Exception
    {
        CmisObject object = createMockedCmisObject(new String[][]{
                {PropertyIds.NAME, "Name", "A document name.txt"},
                {PropertyIds.CONTENT_STREAM_LENGTH, "Content Stream Length", "210"}});

        vc.put("cmisObject", object);
        vc.put("documentLink", "http://www.sourcesense.com");

        String result = render("templates/cmis/doclink.vm");

        assertEquals("[A document name.txt|http://www.sourcesense.com]", result);
    }

    public void testCMISLinkGeneration()
    {
        String result = null;
        try
        {
            Session session = getSession(TEST_REPOSITORY_NAME);
            ItemIterable<CmisObject> children = session.getRootFolder().getChildren();
            for (CmisObject obj : children)
            {
                if ("testcontent.txt".equals(obj.getName()))
                {
                    Document doc = (Document) obj;

                    vc.put("cmisObject", doc);
                    vc.put("documentLink", "http://www.sourcesense.com");

                    result = render ("templates/cmis/doclink.vm");
                }
            }
        }
        catch (CmisRuntimeException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (MacroException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail (e.getMessage());
        }

        assertNotNull(result);
        assertFalse("".equals(result));
        assertEquals ("[testcontent.txt|http://www.sourcesense.com]", result);
        
        logger.debug("result: " + result);
    }
}
