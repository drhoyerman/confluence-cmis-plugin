package com.sourcesense.confluence.cmis;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.velocity.VelocityContext;

import java.util.Map;

public class NavigationMacro extends BaseCMISMacro {

    private static final String TEMPLATE_NAME = "/templates/cmis/cmis-navigation.vm";
    private static final String SERVLET_OUTPUT = "servletOutput";
    private VelocityContext context = null;

    public void setContext(VelocityContext context) {
        this.context = context;
    }

/*
    @Override
    protected String doExecute(Map<String, String> params, String body, RenderContext renderContext, Repository repository) {
        HttpClient client = new HttpClient();
        String servletUrl = params.get("servlet");
        String id = params.get("id");
        String servername = params.get("n");
        if (id == null) {
            id = repository.getConnection(null).getRootFolder().getId();
        }
        HttpMethod method = new GetMethod(servletUrl + "?id=" + id + "&servername=" + servername);
        try {
            client.executeMethod(method);
            if (context == null) {
                context = new VelocityContext(MacroUtils.defaultVelocityContext());
            }
            context.put(SERVLET_OUTPUT, new String(method.getResponseBody()));
            context.put("s", servletUrl);
            context.put("id", id);
            context.put("n", servername);
            return VelocityUtils.getRenderedTemplate(TEMPLATE_NAME, context);
        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {

        } finally {
            method.releaseConnection();
        }
        return null;
    }


    public boolean isInline() {
        return false;
    }
*/

  public RenderMode getBodyRenderMode() {
      return RenderMode.NO_RENDER;
  }

  public boolean hasBody() {
      return false;
  }

    @Override
    protected String executeImpl(Map params, String body, RenderContext renderContext, Session session)
    {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
