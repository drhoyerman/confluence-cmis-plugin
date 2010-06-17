package com.sourcesense.confluence.cmis.macros;

import com.atlassian.renderer.RenderContext;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class FolderExplorerMacroTestCase extends AbstractMacroBaseUnitTestCase
{
    Logger log = Logger.getLogger(FolderExplorerMacroTestCase.class);

    @Test
    public void testBrowseTemplate () throws Exception
    {
        Session session = getSession(TEST_SERVER_NAME);
        Folder rootFolder = session.getRootFolder();

        vc.put("cmisObject", rootFolder);
        vc.put("cmisObjects", rootFolder.getChildren());
        vc.put("documentLink", "http://www.sourcesense.com");

        String result = renderTemplate("templates/cmis/folderbrowser.vm");
        log.info("result:\n" + result);
    }

    @Test
    public void testFolderExplorerMacro()
    {
        try
        {
            FolderExplorerMacro macro = createCMISMockMacro(FolderExplorerMacro.class);

            Map<String, Object> userParams = new HashMap<String, Object>();

            userParams.put(BaseCMISMacro.PARAM_ID, TEST_FOLDER_ID);
            userParams.put(BaseCMISMacro.PARAM_USEPROXY, false);
            userParams.put(BaseCMISMacro.PARAM_RESULTS_NUMBER, 100);
            
            String result = macro.executeImpl (userParams, "", new RenderContext(), confluenceCMISRepository);
            String expectedResult = "" +
                    "||Title||Last Modified||Size||Type||\n" +
                    "|A document name.txt|Thu Jan 01 01:00:00 CET 1970|210|text/plain|\n";

            log.debug("resulting text is:\n" + result);
            assertEquals (expectedResult, result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }
}
