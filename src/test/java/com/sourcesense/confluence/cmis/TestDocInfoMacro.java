package com.sourcesense.confluence.cmis;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
                {"", "nullProperty", null}});

        vc.put("cmisObject", object);
        vc.put("documentLink", "http://www.sourcesense.com");

        String renderedView = render ("templates/cmis/docinfo.vm");

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
}
