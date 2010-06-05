The CMIS Plugin for Atlassian Confluence defines a set of macros that allow Confluence to retrieve
and display information from a CMIS server.
This plugin relies on a snapshot version of Apache Chemistry OpenCMIS - http://incubator.apache.org/chemistry

For more information on CMIS see http://www.cmisdev.org

1. Requirements

+ Maven 2.0.9+ (Maven 2.2.1 is recommended)
+ Atlassian Confluence 3.x
+ Alfresco 3.3 (or any other Content Management System supporting CMIS 1.0 spec)
NOTE! Be sure that you define different port numbers; both platforms bind on port 8080 (and 8000 for shutdown) by default.
You can either edit Alfresco/tomcat/conf/server.xml or confluence-3.2.1_01-std/conf/server.xml

The plugin has been tested on Confluence 3.2.1_01 and Alfresco Community/Enterprise 3.3

2. Building the plugin

From the project's root folder, run:

> mvn clean package

After testing the application against a public CMIS server,
the plugin will be located in target/cmis-confluence-plugin-<version>.jar

If you
- Don't want to run the test
> mvn clean package -DskipTests
- Want to run test against your CMIS server
> mvn clean package -Drealm=http://localhost:8080/alfresco/service/api/cmis -Duser=admin -Dpwd=admin

3. Installing the plugin on Confluence

- Access to the Confluence Admin Plugin interface - http://localhost:8085/admin/viewplugins.action
- Upload the previously built plugin
- Configure the CMIS Plugin and set:
  - ServerName : alfresco
  - http://localhost:8080/alfresco/service/api/cmis
  - Username : admin
  - Password : admin

4. Test it!

- Log into Alfresco - http://localhost:8080/alfresco (admin/admin)
- Upload some content
- Fetch the ID of the content you've just uploaded; by looking at the link of the content
(i.e. http://localhost:8080/alfresco/d/d/workspace/SpacesStore/096a6cc4-9c03-4606-afe0-16278ca484f6/README.txt)
you have to fetch the ID  from the url : workspace://SpacesStore/096a6cc4-9c03-4606-afe0-16278ca484f6
The ID of the item can be safely retrieved from the Alfresco Admin Node Browser
- Add a Page on Confluence (http://localhost:8085/pages/createpage.action?spaceKey=ds) and dump the following code:

h1. Embedding an CMIS Document
{cmis-embed:id=workspace://SpacesStore/096a6cc4-9c03-4606-afe0-16278ca484f6}

h1. Embedding CMIS Document informations
{cmis-docinfo:id=workspace://SpacesStore/096a6cc4-9c03-4606-afe0-16278ca484f6}

h1. Linking to an CMIS Document
{cmis-doclink:id=workspace://SpacesStore/096a6cc4-9c03-4606-afe0-16278ca484f6}

h1. Performs the CMIS-SQL query specified as the body of the macro and displays the results as a table
{cmis-search}SELECT * FROM cmis:document{cmis-search}

5. Additional features

No Format - encloses the document in a {noformat} macro; default is 'yes'
{cmis-embed:id=workspace://SpacesStore/096a6cc4-9c03-4606-afe0-16278ca484f6|nf=no}

Specify another server - you can specify on every macro servername=<your server name>, if you have more than one repository configured
{cmis-embed:servername=alfresco|id=workspace://SpacesStore/096a6cc4-9c03-4606-afe0-16278ca484f6}

Custom properties in the search results - you can specify which properties to show by using
{cmis-search:properties=<name;objectId;objectTypeId;createdBy;creationDate;lastModifiedBy;lastModificationDate}
SELECT * FROM cmis:document
{cmis-search}