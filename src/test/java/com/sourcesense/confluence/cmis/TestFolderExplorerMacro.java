package com.sourcesense.confluence.cmis;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class TestFolderExplorerMacro extends AbstractBaseUnitTest
{
    Logger log = Logger.getLogger(TestFolderExplorerMacro.class);

    public void testBrowseTemplate () throws Exception
    {
        Session session = getSession(TEST_REPOSITORY_NAME);
        Folder rootFolder = session.getRootFolder();

        vc.put("cmisObject", rootFolder);
        vc.put("cmisObjects", rootFolder.getChildren());
        vc.put("documentLink", "http://www.sourcesense.com");

        String result = render ("templates/cmis/folderbrowser.vm");
        log.info("result:\n" + result);
    }
}
