package com.sourcesense.confluence.cmis;

import com.atlassian.renderer.RenderContext;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Carlo Sciolla &lt;c.sciolla@sourcesense.com&gt;
 */
public class TestDocInfoMacro extends AbstractBaseUnitTest
{
    @SuppressWarnings("unchecked")
    public void testRenderDocumentInfo() throws Exception
    {

        CmisObject object = createMockedCmisObject(new String[][]{
                {"fake", "displayName", "value"},
                {PropertyIds.CONTENT_STREAM_LENGTH, "Content Stream Lenght", "210"},
                {PropertyIds.NAME, "Name", "A nice document.txt"},
                {"", "nullProperty", null}}, CmisObject.class);

        vc.put("cmisObject", object);
        vc.put("documentLink", "http://www.sourcesense.com");

        String renderedView = renderTemplate("templates/cmis/docinfo.vm");

        assertNotNull(renderedView);
        assertTrue(renderedView.length() > 0);

        String expectedResult = "*Details of [A nice document.txt|http://www.sourcesense.com]*\n" +
                "||Property||Value||\n" +
                "|nullProperty| |\n" +
                "|Content Stream Lenght|210|\n" +
                "|Name|A nice document.txt|\n" +
                "|displayName|value|\n";

        assertEquals(expectedResult, renderedView);
    }

    public void testDocinfoMacro()
    {
        try
        {
            DocinfoMacro macro = createCMISMockMacro(DocinfoMacro.class);

            Map<String, Object> userParams = new HashMap<String, Object>();

            userParams.put(BaseCMISMacro.PARAM_ID, TEST_DOCUMENT_ID);
            userParams.put(BaseCMISMacro.PARAM_USEPROXY, false);

            String result = macro.executeImpl(userParams, "", new RenderContext(), confluenceCMISRepository);

            // TODO: render the test document
            String expectedResult = "*Details of [A document name.txt|http://www.sourcesense.com]*\n" +
                    "||Property||Value||\n" +
                    "|Content Stream Length|210|\n" +
                    "|Object Type Id|cmis:document|\n" +
                    "|Name|A document name.txt|\n" +
                    "|Content Stream Mime Type|text/plain|\n" +
                    "|Object Type Id|aCmisDocument|\n" +
                    "|Base Type Id|cmis:document|\n" +
                    "|Last Modification Date|Thu Jan 01 01:00:00 CET 1970|\n";

            assertNotNull (result);
            assertEquals(expectedResult, result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
