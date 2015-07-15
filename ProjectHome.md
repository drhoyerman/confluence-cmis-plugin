# What is it #

The **Confluence CMIS Plugin** is an extension for the widely spread collaboration tool [Atlassian Confluence](http://www.atlassian.com/software/confluence/) which allows editors to embed informations coming from other repositories supporting the [CMIS](http://en.wikipedia.org/wiki/Content_Management_Interoperability_Services) specification.

---


# Why shall I use it #
Wiki frameworks have introduced a very straight approach to knowledge-sharing; they (commonly) provide a very easy, simple and straight interface to fill contents in, avoiding _complex_ templates and processes that can potentially discourage users to share their knowledge.

On the other hand, contents filled in a Wiki are not structured (in most cases) and lack (sometimes) an _**enterprisey**_ support for documents, assets and structured content.

This specific use case needs an integrated solution which allow
  * _**Users to edit the contents in the way they prefer**_ - typically the Wiki way
  * _**Assets/documents to be handled (not only stored) by ECM systems**_

The Confluence CMIS Plugin aims to fill this gap.

---


# How it works #

The Confluence CMIS Plugin provides a way to configure one or more CMIS Repositories as sources using the Confluence Administration Console; by using a predefined set of macros (see below) an editor can embed any content coming from the CMIS Repository.

The content rendered in the front page of a Confluence document is
  1. Requested by a Confluence macro (i.e. `{cmis-doclink:id=workspace://whatever/123456`})
  1. Intercepted by the CMIS Plugin which invokes the [OpenCMIS client API](http://incubator.apache.org/chemistry/opencmis.html)
  1. Rendered as Wiki Markup language to the Confluence rendering engine
  1. Delivered to the end user as HTML

Each link that refers to CMIS Documents is proxied via a Java Servlet deployed on Confluence (and shipped with the plugin); if you want, for example, to render a link to a PDF file stored in your ECM (say `http://localhost:8080/alfresco/service/cmis/s/workspace:SpacesStore/i/096a6cc4-9c03-4606-afe0-16278ca484f6/README.txt`), you want the link to point to the Confluence server - since you can access to it - and not directly to the ECM system, which might be behind firewall (or you might access through a strict proxy); this is why the Confluence CMIS plugins will reroute the links for you (in this case - `http://localhost:8085/confluence/plugins/servlet/CMISProxy/alfresco/service/cmis/s/workspace:SpacesStore/i/096a6cc4-9c03-4606-afe0-16278ca484f6/content.txt?servername=alfresco`)

You can still avoid the usage of the `ProxyServlet` by using `useproxy=no` as a parameter of your macros; for more details, check our [README](http://confluence-cmis-plugin.googlecode.com/svn/trunk/README.txt).

Enough talking, let's see it in action!

---


## Configure your plugin ##
Access the Configuration perspective from the Confluence Administration panel and define your connection with the CMIS Repository; as you can see, the _Repository ID_ is optional and should be used only if you want to connect with a specific CMIS repository (in case of multiple) of the configured realm.
The _Search Configuration_ checkboxes define the default CMIS properties that must be rendered using the `{cmis-search`} macro.

![http://confluence-cmis-plugin.googlecode.com/svn/trunk/docs/Configure%20Plugin.png](http://confluence-cmis-plugin.googlecode.com/svn/trunk/docs/Configure%20Plugin.png)

---


## Fetch a Document reference within the CMIS Server ##
By browsing your CMIS-enabled repository (in this example Alfresco 3.3 Community) we can easily fetch its reference (currently the `id`)

![http://confluence-cmis-plugin.googlecode.com/svn/trunk/docs/Selecting%20a%20Document%20reference.png](http://confluence-cmis-plugin.googlecode.com/svn/trunk/docs/Selecting%20a%20Document%20reference.png)

---


## Edit your Confluence page ##
You can now use Confluence CMIS Macros to show the content previously located

![http://confluence-cmis-plugin.googlecode.com/svn/trunk/docs/Editing%20Confluence%20page%20to%20embed%20CMIS%20contents.png](http://confluence-cmis-plugin.googlecode.com/svn/trunk/docs/Editing%20Confluence%20page%20to%20embed%20CMIS%20contents.png)

---


## Enjoy the result ##
This is the result; if you want to know more about features and syntax, checkout our [README](http://confluence-cmis-plugin.googlecode.com/svn/trunk/README.txt)

![http://confluence-cmis-plugin.googlecode.com/svn/trunk/docs/Confluence%20page%20that%20embeds%20CMIS%20contents.png](http://confluence-cmis-plugin.googlecode.com/svn/trunk/docs/Confluence%20page%20that%20embeds%20CMIS%20contents.png)

---


# Downloads and releases #
You can find a nightly build release in the Downloads section.
The nightly builds (SNAPSHOT) are provided by the [Sourcesense Repository Manager](http://repository.sourcesense.com/nexus/content/repositories/sose.public.snapshots/com/sourcesense/confluence/cmis-confluence-plugin/); we're working to provide a full release (and a clearer roadmap) soon.

---


# Dependencies #

This project makes use of
  1. The standard [Confluence Development Plugin Kit](http://confluence.atlassian.com/display/CONF26/Confluence+Plugin+Development+Kit)
  1. [OpenCMIS 0.1-SNAPSHOT](http://incubator.apache.org/chemistry/) - we're waiting for their first official release in order to bundle the first official version of this plugin.

---


# Testing #

The plugin has been tested with
  * Confluence 3.2.1\_01
  * Alfresco 3.3 Community (not tested with Enterprise yet, but expected to work the same of Community)
  * Maven 2.0.9+

You can run a quick test of the Plugin with the following simple commands
```
svn co http://confluence-cmis-plugin.googlecode.com/svn/trunk/ confluence-cmis-plugin
cd confluence-cmis-plugin && mvn clean test
```
The first time it might take a while to download Maven dependencies, but the testing phase goes smooth and doesn't need any CMIS Server or Confluence to be up and running locally (test is performed using cmis.alfresco.com which adheres with the CMIS 1.0 specs)

---


# What's next #

Checkout our [TODO list](http://confluence-cmis-plugin.googlecode.com/svn/trunk/TODO.txt) and join our [Discussion Group](http://groups.google.com/group/confluence-cmis) to get involved! We're looking for some feedback in order to establish a better roadmap, based on the mostly popular use cases.