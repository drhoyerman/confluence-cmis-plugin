The CMIS Plugin for Atlassian Confluence defines a set of macros that allow Confluence to retrieve
and display information from a CMIS server.

For more information on CMIS see http://www.cmisdev.org

1. Building the plugin

Maven 2.0.9+ is required. Maven 2.1.0 is recommended.

This plugin relies on an unreleased version of Apache Abdera, therefore you need to build it yourself.

Checkout http://svn.apache.org/repos/asf/abdera/java/trunk and run "mvn install". If the build was
successful, you should have now a full set of Abdera librries in your local Maven repository.

Build the plugin by running "mvn package" in this directory. At the end of the build, you will find
the plugin's JAR in the "target" directory. Install it using Confluence's plugin manager as usual.

As of June 4, 2009, the plugin has only been tested with Confluence 3.0-beta2-r3.

** NOTE ON RESOURCE FILTERING **

The default pom has 'resource filtering' enabled, which means files in the src/main/resources directory will have
variables in the form ${var} replaced during the build process. For example, the default atlassian-plugin.xml includes
${project.artifactId}, which is replaced with the artifactId taken from the POM when building the plugin.

More information on resource filtering is available in the Maven documentation:

http://maven.apache.org/plugins/maven-resources-plugin/examples/filter.html


2. Using the plugin

The plugin defines the following macros:

{cmis-link:s=<url>|u=<username>|p=<password>}

Displays a link to the document or folder whose CMIS URL is <url>.

{cmis-link:s=<url>|id=<id>|u=<username>|p=<password>}

Displays a link to the document or folder whose object ID is <id>. In this case, the value of "s"
must be equal to the URL of the CMIS Service Document.

{cmis-embed:s=<url>|u=<username>|p=<password>|nf=<yes|no>}

Embeds the document whose CMIS URL is <url> in the current page. If the value of the "nf" parameter
starts with "y", encloses the document in a {noformat} macro.

{cmis-embed:s=<url>|id=<id>|u=<username>|p=<password>|nf=<yes|no>}

Embeds the document whose object ID is <id> in the current page. In this case, the value of "s"
must be equal to the URL of the CMIS Service Document.

If the value of the "nf" parameter starts with "y", encloses the document in a {noformat} macro.

{cmis-search:s=<url>|u=<username>|p=<password>}
SELECT ...
{cmis-search}

Performs the CMIS-SQL query specified as the body of the macro and displays the results as a table.
The value of the "s" parameter must be the URL of the CMIS Service Document.
 
