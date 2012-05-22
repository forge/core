/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.scaffold.faces;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.scaffold.faces.metawidget.widgetbuilder.EntityWidgetBuilder;
import org.jboss.forge.scaffold.faces.metawidget.widgetbuilder.EntityWidgetBuilderConfig;
import org.jboss.forge.shell.exceptions.PluginExecutionException;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.ReadOnlyWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.richfaces.RichFacesWidgetBuilder;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilder;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilderConfig;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */

@RunWith(Arquillian.class)
public class FacesScaffoldTest extends AbstractFacesScaffoldTest
{
	
	private final String crlf = "\r\n";
	
    @Test
    public void testScaffoldSetup() throws Exception
    {
       Project project = setupScaffoldProject();
       ServletFacet servlet = project.getFacet(ServletFacet.class);

       Assert.assertTrue(project.hasFacet(FacesScaffold.class));

       Node root = XMLParser.parse(servlet.getConfigFile().getResourceInputStream());
       List<Node> errorPages = root.get("error-page");
       Assert.assertEquals("/faces/error.xhtml", errorPages.get(0).getSingle("location").getText());

       WebResourceFacet web = project.getFacet(WebResourceFacet.class);
       FileResource<?> error = web.getWebResource("error.xhtml");
       Assert.assertTrue(Streams.toString(error.getResourceInputStream()).contains(
                "/resources/scaffold/pageTemplate.xhtml"));

       // Test page exists, but has no navigation

       FileResource<?> page = web.getWebResource("/resources/scaffold/pageTemplate.xhtml");
       Assert.assertTrue(page.exists());
       String contents = Streams.toString(page.getResourceInputStream());
       Assert.assertTrue(contents.contains(
                "<div id=\"wrapper\">"));
       Assert.assertTrue(contents.contains(
                "<div id=\"navigation\">"));
       Assert.assertTrue(contents.contains(
                "<div id=\"content\">"));
       Assert.assertTrue(contents.contains(
                "<div id=\"footer\">"));
       Assert.assertTrue(!contents.contains(
                "<h:link outcome=\"/>"));
    }

   @Test
   public void testScaffoldSetupWithScaffoldTypeWithoutTargetDir() throws Exception
   {
       Project project = initializeJavaProject();
       queueInputLines("HIBERNATE", "JBOSS_AS7", "", "");
       getShell().execute("persistence setup");
       queueInputLines("", "", "2", "", "", "");
       getShell().execute("scaffold setup --scaffoldType faces");
       Assert.assertTrue(project.hasFacet(FacesScaffold.class));
   }

   @Test
   public void testScaffoldSetupWithScaffoldTypeAndTargetDir() throws Exception
   {
       Project project = initializeJavaProject();
       queueInputLines("HIBERNATE", "JBOSS_AS7", "", "");
       getShell().execute("persistence setup");
       queueInputLines("", "", "2", "", "", "");
       getShell().execute("scaffold setup --scaffoldType faces --targetDir store");
       Assert.assertTrue(project.hasFacet(FacesScaffold.class));
   }

   @Test(expected = PluginExecutionException.class)
   public void testCannotGenerateFromEntityUntilScaffoldInstalled() throws Exception
   {
      initializeJavaProject();

      queueInputLines("", "");
      getShell().execute("persistence setup --provider HIBERNATE  --container JBOSS_AS7");

      queueInputLines("");
      getShell().execute("entity --named Customer");

      queueInputLines("", "");
      getShell().execute("scaffold from-entity");
   }

   @Test
   public void testGenerateFromEntity() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");

      queueInputLines("", "");
      getShell().execute("scaffold from-entity");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      StringBuilder metawidget = new StringBuilder();
      metawidget.append("\t")    .append("<ui:define name=\"main\">"                                                                            ).append(crlf);
      metawidget.append("\t\t")  .append(  "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                             ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                     ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>").append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText/>"                                                                                  ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                       ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"  ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText/>"                                                                                  ).append(crlf);
      metawidget.append("\t\t")  .append(  "</h:panelGrid>"                                                                                     ).append(crlf);
      
      Assert.assertTrue(contents.contains(metawidget));

      // Create

      FileResource<?> create = web.getWebResource("/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      metawidget = new StringBuilder();
      
      metawidget.append("\t\t"       ).append("<h:form id=\"create\">"                                                                                ).append(crlf);
      metawidget.append("\t\t\t"     ).append(  "<h:messages globalOnly=\"true\" styleClass=\"error\"/>"                                              ).append(crlf);
      metawidget.append(""           ).append(  ""                                                                                                    ).append(crlf);
      metawidget.append("\t\t\t"     ).append(  "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                              ).append(crlf);
      metawidget.append("\t\t\t\t"   ).append(    "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                      ).append(crlf);
      metawidget.append("\t\t\t\t"   ).append(    "<h:panelGroup>"                                                                                    ).append(crlf);
      metawidget.append("\t\t\t\t\t" ).append(      "<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>").append(crlf);
      metawidget.append("\t\t\t\t\t" ).append(      "<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>"                         ).append(crlf);
      metawidget.append("\t\t\t\t"   ).append(    "</h:panelGroup>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"   ).append(    "<h:outputText/>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"   ).append(    "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                        ).append(crlf);
      metawidget.append("\t\t\t\t"   ).append(    "<h:panelGroup>"                                                                                    ).append(crlf);
      metawidget.append("\t\t\t\t\t" ).append(      "<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"  ).append(crlf);
      metawidget.append("\t\t\t\t\t" ).append(      "<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>"                          ).append(crlf);
      metawidget.append("\t\t\t\t"   ).append(    "</h:panelGroup>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"   ).append(    "<h:outputText/>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t"     ).append(  "</h:panelGrid>"                                                                                      ).append(crlf);

      Assert.assertTrue(contents.contains(metawidget));

      // Search

      FileResource<?> search = web.getWebResource("/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      StringBuilder searchMetawidget = new StringBuilder();
      searchMetawidget.append("\t\t"        ).append("<h:form id=\"search\">"                                                                              ).append(crlf);
      searchMetawidget.append("\t\t\t"      ).append(  "<h:panelGroup styleClass=\"search\">"                                                              ).append(crlf);
      searchMetawidget.append(""            ).append(  ""                                                                                                  ).append(crlf);
      searchMetawidget.append("\t\t\t\t"    ).append(    "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchFirstName\" value=\"First Name:\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                                ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchFirstName\" value=\"#{customerBean.search.firstName}\"/>").append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchFirstName\" styleClass=\"error\"/>"                       ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchLastName\" value=\"Last Name:\"/>"                      ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                                ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchLastName\" value=\"#{customerBean.search.lastName}\"/>"  ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchLastName\" styleClass=\"error\"/>"                        ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t"    ).append(    "</h:panelGrid>"                                                                                  ).append(crlf);

      Assert.assertTrue(contents.contains(searchMetawidget));

      StringBuilder beanMetawidget = new StringBuilder();
      beanMetawidget.append("\t\t\t"      ).append("<h:dataTable id=\"customerBeanPageItems\" styleClass=\"data-table\" value=\"#{customerBean.pageItems}\" var=\"_item\">").append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "<h:column>"                                                                                                          ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<f:facet name=\"header\">"                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText value=\"First Name\"/>"                                                                            ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</f:facet>"                                                                                                        ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<h:link outcome=\"/customer/view\">"                                                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                                                    ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText id=\"itemFirstName\" value=\"#{_item.firstName}\"/>"                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</h:link>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "</h:column>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "<h:column>"                                                                                                          ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<f:facet name=\"header\">"                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText value=\"Last Name\"/>"                                                                             ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</f:facet>"                                                                                                        ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<h:link outcome=\"/customer/view\">"                                                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                                                    ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText id=\"itemLastName\" value=\"#{_item.lastName}\"/>"                                                 ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</h:link>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "</h:column>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t"      ).append("</h:dataTable>"                                                                                                        ).append(crlf);

      Assert.assertTrue(contents.contains(beanMetawidget));

      // Backing Bean

      FileResource<?> customerBean = java.getJavaResource("/com/test/view/CustomerBean.java");
      Assert.assertTrue(customerBean.exists());
      contents = Streams.toString(customerBean.getResourceInputStream());

      Assert.assertTrue(contents.contains("\tprivate Customer customer;"));

      StringBuilder qbeMetawidget = new StringBuilder();
      qbeMetawidget.append("\t\t"  ).append("List<Predicate> predicatesList = new ArrayList<Predicate>();"                               ).append(crlf);
      qbeMetawidget.append(""      ).append(""                                                                                           ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("String firstName = this.search.getFirstName();"                                             ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("if (firstName != null && !\"\".equals(firstName)) {"                                        ).append(crlf);
      qbeMetawidget.append("\t\t\t").append(  "predicatesList.add(builder.like(root.<String>get(\"firstName\"), '%' + firstName + '%'));").append(crlf);
      qbeMetawidget.append("\t\t"  ).append("}"                                                                                          ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("String lastName = this.search.getLastName();"                                               ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("if (lastName != null && !\"\".equals(lastName)) {"                                          ).append(crlf);
      qbeMetawidget.append("\t\t\t").append(  "predicatesList.add(builder.like(root.<String>get(\"lastName\"), '%' + lastName + '%'));"  ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("}"                                                                                          ).append(crlf);
      qbeMetawidget.append(""      ).append(""                                                                                           ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("return "                                                                                    );

      Assert.assertTrue(contents.contains(qbeMetawidget));

      Assert.assertTrue(contents.contains("\tprivate Customer add = new Customer();"));
      Assert.assertTrue(contents.contains("\t\tthis.add = new Customer();"));

      // ViewUtils

      FileResource<?> viewUtils = java.getJavaResource("/com/test/view/ViewUtils.java");
      Assert.assertTrue(viewUtils.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/pageTemplate.xhtml"));

      FileResource<?> taglib = web.getWebResource("WEB-INF/classes/META-INF/forge.taglib.xml");
      Assert.assertTrue(taglib.exists());
      contents = Streams.toString(taglib.getResourceInputStream());
      Assert.assertTrue(contents.contains("<function-class>com.test.view.ViewUtils</function-class>"));

      // Additional files

      Assert.assertTrue(web.getWebResource("resources/add.png").exists());
      Assert.assertTrue(web.getWebResource("resources/background.gif").exists());
      Assert.assertTrue(web.getWebResource("resources/false.png").exists());
      Assert.assertTrue(web.getWebResource("resources/favicon.ico").exists());
      Assert.assertTrue(web.getWebResource("resources/forge-logo.png").exists());

      FileResource<?> css = web.getWebResource("resources/forge-style.css");
      Assert.assertTrue(css.exists());
      contents = Streams.toString(css.getResourceInputStream());
      Assert.assertTrue(contents.contains("#content .error {"));

      Assert.assertTrue(web.getWebResource("resources/jboss-community.png").exists());
      Assert.assertTrue(web.getWebResource("resources/remove.png").exists());
      Assert.assertTrue(web.getWebResource("resources/search.png").exists());
      Assert.assertTrue(web.getWebResource("resources/true.png").exists());
      Assert.assertTrue(web.getWebResource("resources/scaffold/pageTemplate.xhtml").exists());

      FileResource<?> paginator = web.getWebResource("resources/scaffold/paginator.xhtml");
      Assert.assertTrue(paginator.exists());
      contents = Streams.toString(paginator.getResourceInputStream());

      // Paginator should use commandLink, not outputLink, else search criteria gets lost on page change

      Assert.assertTrue(contents.contains("<h:commandLink "));
      Assert.assertTrue(!contents.contains("<h:outputLink "));
   }

   @Test
   public void testGenerateFromEntityCamelCase() throws Exception
   {
	   
	  StringBuilder expectedContent;
      
	  Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named CustomerPerson");
      getShell().execute("field string --named name");

      queueInputLines("", "");
      getShell().execute("scaffold from-entity");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("/customerPerson/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());

      expectedContent = new StringBuilder();      
      expectedContent.append("<ui:param name=\"pageTitle\" value=\"View Customer Person\"/>");
      Assert.assertTrue(contents.contains(expectedContent));
      
      expectedContent = new StringBuilder();
      expectedContent.append("\t"  ).append("<ui:define name=\"header\">").append(crlf);
      expectedContent.append("\t\t").append(  "Customer Person"          ).append(crlf);
      expectedContent.append("\t"  ).append("</ui:define>"               );
      Assert.assertTrue(contents.contains(expectedContent));
      
      expectedContent = new StringBuilder();
      expectedContent.append("\t"  ).append("<ui:define name=\"subheader\">" ).append(crlf);
      expectedContent.append("\t\t").append(  "View existing Customer Person").append(crlf);
      expectedContent.append("\t"  ).append("</ui:define>"                   );
      Assert.assertTrue(contents.contains(expectedContent));
      
      expectedContent = new StringBuilder();      
      expectedContent.append("customerPersonBean.customerPerson");      
      Assert.assertTrue(contents.contains(expectedContent));

      // Create

      FileResource<?> create = web.getWebResource("/customerPerson/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());

      expectedContent = new StringBuilder();      
      expectedContent.append("<ui:param name=\"pageTitle\" value=\"Create Customer Person\"/>");      
      Assert.assertTrue(contents.contains(expectedContent));

      expectedContent = new StringBuilder();
      expectedContent.append("\t"  ).append("<ui:define name=\"header\">").append(crlf);
      expectedContent.append("\t\t").append(  "Customer Person"          ).append(crlf);
      expectedContent.append("\t"  ).append("</ui:define>"               );
      Assert.assertTrue(contents.contains(expectedContent));

      expectedContent = new StringBuilder();
      expectedContent.append("Edit existing Customer Person");
      Assert.assertTrue(contents.contains(expectedContent));

      expectedContent = new StringBuilder();      
      expectedContent.append("Create a new Customer Person");
      Assert.assertTrue(contents.contains(expectedContent));

      expectedContent = new StringBuilder();      
      expectedContent.append("customerPersonBean.customerPerson");
      Assert.assertTrue(contents.contains(expectedContent));

      // Search

      FileResource<?> search = web.getWebResource("/customerPerson/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());

      expectedContent = new StringBuilder();      
      expectedContent.append("<ui:param name=\"pageTitle\" value=\"Search Customer Person entities\"/>");
      Assert.assertTrue(contents.contains(expectedContent));
      
      expectedContent = new StringBuilder();      
      expectedContent.append("\t"  ).append("<ui:define name=\"header\">").append(crlf);
      expectedContent.append("\t\t").append(  "Customer Person"          ).append(crlf);
      expectedContent.append("\t"  ).append("</ui:define>"               );
      Assert.assertTrue(contents.contains(expectedContent));

      expectedContent = new StringBuilder();      
      expectedContent.append("\t"  ).append("<ui:define name=\"subheader\">" ).append(crlf);
      expectedContent.append("\t\t").append(  "Search Customer Person entities").append(crlf);
      expectedContent.append("\t"  ).append("</ui:define>"                   );
      Assert.assertTrue(contents.contains(expectedContent));

      expectedContent = new StringBuilder();      
      expectedContent.append("customerPersonBean.pageItems");
      Assert.assertTrue(contents.contains(expectedContent));

      // Navigation

      FileResource<?> navigation = web.getWebResource("resources/scaffold/pageTemplate.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());
      expectedContent = new StringBuilder();      
      expectedContent.append("<h:link outcome=\"/customerPerson/search\" value=\"Customer Person\"/>");
      Assert.assertTrue(contents.contains(expectedContent));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGenerateFromEntityWithTemplate() throws Exception
   {
      Project project = setupScaffoldProject();
      queueInputLines("");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      web.createWebResource("<ui:insert name=\"main\">", "test-template.xhtml");

      queueInputLines("", "");
      getShell().execute(
               "scaffold from-entity --usingTemplate "
                        + web.getWebResource("test-template.xhtml").getFullyQualifiedName());

      FileResource<?> view = web.getWebResource("/customer/view.xhtml");
      FileResource<?> create = web.getWebResource("/customer/create.xhtml");
      FileResource<?> search = web.getWebResource("/customer/search.xhtml");

      for (FileResource<?> file : Arrays.asList(view, create, search))
      {
         Assert.assertTrue(file.exists());
         Assert.assertTrue(Streams.toString(file.getResourceInputStream()).contains(
                  "template=\"/test-template.xhtml"));
      }

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource bean = java.getJavaResource(java.getBasePackage() + ".view.CustomerBean");
      Assert.assertTrue(bean.exists());

      getShell().execute("build");
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGenerateFromEntityWithUnsupportedTemplate() throws Exception
   {
      Project project = setupScaffoldProject();
      queueInputLines("");
      getShell().execute("entity --named Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      web.createWebResource("<ui:insert name=\"other\">", "test-template.xhtml");

      queueInputLines("", "");

      try
      {
         getShell().execute(
                  "scaffold from-entity --usingTemplate "
                           + web.getWebResource("test-template.xhtml").getFullyQualifiedName());
         fail();
      }
      catch (IllegalStateException e)
      {
         FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
         FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
         FileResource<?> search = web.getWebResource("scaffold/customer/search.xhtml");

         for (FileResource<?> file : Arrays.asList(view, create, search))
         {
            Assert.assertFalse(file.exists());
         }
      }
   }

   @Test
   public void testGenerateFromNestedEntity() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Address");
      getShell().execute("field string --named street");
      getShell().execute("field string --named city");
      getShell().execute("field string --named state");
      getShell().execute("field string --named zip");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      queueInputLines("com.test.model.Address");
      getShell().execute("field custom --named address");

      queueInputLines("", "");
      getShell().execute("scaffold from-entity");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      // Create

      FileResource<?> create = web.getWebResource("/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));
      
      StringBuilder metawidget = new StringBuilder();
      metawidget.append("\t\t"        ).append("<h:form id=\"create\">"                                                                                           ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:messages globalOnly=\"true\" styleClass=\"error\"/>"                                                         ).append(crlf);
      metawidget.append(""            ).append(""                                                                                                                 ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                                         ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                                 ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:panelGroup>"                                                                                               ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>"           ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>"                                    ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "</h:panelGroup>"                                                                                              ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputText/>"                                                                                              ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                                   ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:panelGroup>"                                                                                               ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"             ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>"                                     ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "</h:panelGroup>"                                                                                              ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputText/>"                                                                                              ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputLabel for=\"customerBeanCustomerAddress\" value=\"Address:\"/>"                                      ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\" id=\"customerBeanCustomerAddress\">"    ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanCustomerAddressStreet\" value=\"Street:\"/>"                               ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanCustomerAddressStreet\" value=\"#{customerBean.customer.address.street}\"/>").append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanCustomerAddressStreet\" styleClass=\"error\"/>"                              ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanCustomerAddressCity\" value=\"City:\"/>"                                   ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanCustomerAddressCity\" value=\"#{customerBean.customer.address.city}\"/>"    ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanCustomerAddressCity\" styleClass=\"error\"/>"                                ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanCustomerAddressState\" value=\"State:\"/>"                                 ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanCustomerAddressState\" value=\"#{customerBean.customer.address.state}\"/>"  ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanCustomerAddressState\" styleClass=\"error\"/>"                               ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanCustomerAddressZip\" value=\"Zip:\"/>"                                     ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanCustomerAddressZip\" value=\"#{customerBean.customer.address.zip}\"/>"      ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanCustomerAddressZip\" styleClass=\"error\"/>"                                 ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "</h:panelGrid>"                                                                                               ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputText/>"                                                                                              ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "</h:panelGrid>"                                                                                                 ).append(crlf);
      Assert.assertTrue(contents.contains(metawidget));

      // Search

      FileResource<?> search = web.getWebResource("/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      // Test regeneration

      queueInputLines("", "", "", "", "", "");
      getShell().execute("scaffold from-entity");

      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains(metawidget));
   }

   @Test
   public void testGenerateManyToOneEntity() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Employer");
      getShell().execute("field string --named name");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field manyToOne --named employer --fieldType com.test.model.Employer");

      // (need to specify both entities until https://issues.jboss.org/browse/FORGE-392)

      queueInputLines("", "");
      getShell().execute("scaffold from-entity com.test.model.Employer com.test.model.Customer");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));


      StringBuilder metawidget = new StringBuilder();
      metawidget.append("\t\t"    ).append("<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                                               ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                                       ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>"                  ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:outputText/>"                                                                                                    ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                                         ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"                    ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:outputText/>"                                                                                                    ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:outputLabel for=\"customerBeanCustomerEmployer\" value=\"Employer:\"/>"                                          ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:link id=\"customerBeanCustomerEmployer\" outcome=\"/employer/view\" value=\"#{customerBean.customer.employer}\">").append(crlf);
      metawidget.append("\t\t\t\t").append(    "<f:param name=\"id\" value=\"#{customerBean.customer.employer.id}\"/>"                                            ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "</h:link>"                                                                                                          ).append(crlf);
      metawidget.append("\t\t\t"  ).append(  "<h:outputText/>"                                                                                                    ).append(crlf);
      metawidget.append("\t\t"    ).append("</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));

      // Create

      FileResource<?> create = web.getWebResource("/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      metawidget = new StringBuilder();
      metawidget.append("\t\t\t"      ).append("<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                                                                       ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                                                               ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:panelGroup>"                                                                                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(    "<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>"                                         ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(    "<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>"                                                                  ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "</h:panelGroup>"                                                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:outputText/>"                                                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                                                                 ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:panelGroup>"                                                                                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(    "<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"                                           ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(    "<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>"                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "</h:panelGroup>"                                                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:outputText/>"                                                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:outputLabel for=\"customerBeanCustomerEmployer\" value=\"Employer:\"/>"                                                                  ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:panelGroup>"                                                                                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(    "<h:selectOneMenu converter=\"#{employerBean.converter}\" id=\"customerBeanCustomerEmployer\" value=\"#{customerBean.customer.employer}\">").append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(      "<f:selectItem/>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(      "<f:selectItems value=\"#{employerBean.all}\"/>"                                                                                         ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(    "</h:selectOneMenu>"                                                                                                                       ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(    "<h:message for=\"customerBeanCustomerEmployer\" styleClass=\"error\"/>"                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "</h:panelGroup>"                                                                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(  "<h:outputText/>"                                                                                                                            ).append(crlf);
      metawidget.append("\t\t\t"      ).append("</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));

      // Search

      FileResource<?> search = web.getWebResource("/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      // Navigation

      FileResource<?> navigation = web.getWebResource("resources/scaffold/pageTemplate.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());

      StringBuilder navigationText = new StringBuilder();
      navigationText.append("\t\t\t\t"    ).append("<ul>"                                                         ).append(crlf);
      navigationText.append("\t\t\t\t\t"  ).append(  "<li>"                                                       ).append(crlf);
      navigationText.append("\t\t\t\t\t\t").append(    "<h:link outcome=\"/customer/search\" value=\"Customer\"/>").append(crlf);
      navigationText.append("\t\t\t\t\t"  ).append(  "</li>"                                                      ).append(crlf);
      navigationText.append("\t\t\t\t\t"  ).append(  "<li>"                                                       ).append(crlf);
      navigationText.append("\t\t\t\t\t\t").append(    "<h:link outcome=\"/employer/search\" value=\"Employer\"/>").append(crlf);
      navigationText.append("\t\t\t\t\t"  ).append(  "</li>"                                                      ).append(crlf);

      Assert.assertTrue(contents.contains(navigationText));

      // Backing Bean

      FileResource<?> customerBean = java.getJavaResource("/com/test/view/CustomerBean.java");
      Assert.assertTrue(customerBean.exists());
      contents = Streams.toString(customerBean.getResourceInputStream());

      StringBuilder qbeMetawidget = new StringBuilder();
      qbeMetawidget.append("\t\t"  ).append("Employer employer = this.search.getEmployer();"                        ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("if (employer != null) {"                                               ).append(crlf);
      qbeMetawidget.append("\t\t\t").append(  "predicatesList.add(builder.equal(root.get(\"employer\"), employer));").append(crlf);
      qbeMetawidget.append("\t\t"  ).append("}"                                                                     ).append(crlf);

      Assert.assertTrue(contents.contains(qbeMetawidget));
      
      StringBuilder expectedContent = new StringBuilder();
      expectedContent.append("import com.test.model.Customer;").append(crlf);
      expectedContent.append("import com.test.model.Employer;").append(crlf);
      Assert.assertTrue(contents.contains(expectedContent));
   }

   @Test
   public void testGenerateOneToManyEntity() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Grocery");
      getShell().execute("field string --named name");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field oneToMany --named groceries --fieldType com.test.model.Grocery");

      // (need to specify both entities until https://issues.jboss.org/browse/FORGE-392)

      queueInputLines("", "");
      getShell().execute("scaffold from-entity com.test.model.Grocery com.test.model.Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      StringBuilder metawidget = new StringBuilder();
      metawidget.append("\t\t"        ).append("<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                             ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                     ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>").append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:outputText/>"                                                                                  ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                       ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"  ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:outputText/>"                                                                                  ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:outputLabel for=\"customerBeanCustomerGroceries\" value=\"Groceries:\"/>"                      ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:dataTable " +
      													"id=\"customerBeanCustomerGroceries\" " +
      													"styleClass=\"data-table\" " +
      													"value=\"#{forgeview:asList(customerBean.customer.groceries)}\" " +
      													"var=\"_item\">"                                                                            ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:column>"                                                                                     ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<f:facet name=\"header\">"                                                                    ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:outputText value=\"Name\"/>"                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "</f:facet>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:link outcome=\"/grocery/view\">"                                                           ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                               ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<h:outputText id=\"itemName\" value=\"#{_item.name}\"/>"                                    ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "</h:link>"                                                                                    ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "</h:column>"                                                                                    ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "</h:dataTable>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:outputText/>"                                                                                  ).append(crlf);
      metawidget.append("\t\t"        ).append("</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));
      Assert.assertTrue(contents.contains("xmlns:forgeview=\"http://jboss.org/forge/view\""));

      // Create

      FileResource<?> create = web.getWebResource("/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      metawidget = new StringBuilder();
      metawidget.append("\t\t\t"          ).append("<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                                                           ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:panelGroup>"                                                                                                                         ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>"                                     ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>"                                                              ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "</h:panelGroup>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:outputText/>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                                                             ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:panelGroup>"                                                                                                                         ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"                                       ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>"                                                               ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "</h:panelGroup>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:outputText/>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:outputLabel for=\"customerBeanCustomerGroceries\" value=\"Groceries:\"/>"                                                            ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:panelGroup>"                                                                                                                         ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "<ui:param name=\"_collection\" value=\"#{customerBean.customer.groceries}\"/>"                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "<h:dataTable id=\"customerBeanCustomerGroceries\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">").append(crlf);
      metawidget.append("\t\t\t\t\t\t"    ).append(      "<h:column>"                                                                                                                         ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t"  ).append(        "<f:facet name=\"header\">"                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t\t").append(          "<h:outputText value=\"Name\"/>"                                                                                                 ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t"  ).append(        "</f:facet>"                                                                                                                       ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t"  ).append(        "<h:link outcome=\"/grocery/view\">"                                                                                               ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t\t").append(          "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t\t").append(          "<h:outputText id=\"itemName\" value=\"#{_item.name}\"/>"                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t"  ).append(        "</h:link>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t\t"    ).append(      "</h:column>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t\t"    ).append(      "<h:column footerClass=\"remove-column\" headerClass=\"remove-column\">"                                                             ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t"  ).append(        "<h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"remove-button\"/>"                                            ).append(crlf);
      metawidget.append("\t\t\t\t\t\t"    ).append(      "</h:column>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "</h:dataTable>"                                                                                                                       ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "<h:panelGrid columnClasses=\",remove-column\" columns=\"2\" styleClass=\"data-table-footer\">"                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t\t"    ).append(      "<h:selectOneMenu " +
      															"converter=\"#{groceryBean.converter}\" " +
      															"id=\"customerBeanCustomerGroceriesSelect\" " +
      															"value=\"#{requestScope['customerBeanCustomerGroceriesSelect']}\">"                                                           ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t"  ).append(        "<f:selectItem/>"                                                                                                                  ).append(crlf);
      metawidget.append("\t\t\t\t\t\t\t"  ).append(        "<f:selectItems value=\"#{groceryBean.all}\"/>"                                                                                    ).append(crlf);
      metawidget.append("\t\t\t\t\t\t"    ).append(      "</h:selectOneMenu>"                                                                                                                 ).append(crlf);
      metawidget.append("\t\t\t\t\t\t"    ).append(      "<h:commandLink " +
      															"action=\"#{_collection.add(requestScope['customerBeanCustomerGroceriesSelect'])}\" " +
      															"id=\"customerBeanCustomerGroceriesAdd\" " +
      															"onclick=\"if (document.getElementById(document.forms[0].id+':customerBeanCustomerGroceriesSelect').selectedIndex &lt; 1) { alert('Must select a Grocery'); return false; }\" " +
    		  													"styleClass=\"add-button\"/>"                                                                                                 ).append(crlf);
      metawidget.append("\t\t\t\t\t"      ).append(    "</h:panelGrid>"                                                                                                                       ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "</h:panelGroup>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t\t"        ).append(  "<h:outputText/>"                                                                                                                        ).append(crlf);
      metawidget.append("\t\t\t"          ).append("</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));
      Assert.assertTrue(contents.contains("xmlns:forgeview=\"http://jboss.org/forge/view\""));

      // Search

      FileResource<?> search = web.getWebResource("/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      StringBuilder searchMetawidget = new StringBuilder();
      searchMetawidget.append("\t\t"        ).append("<h:form id=\"search\">"                                                                              ).append(crlf);
      searchMetawidget.append("\t\t\t"      ).append(  "<h:panelGroup styleClass=\"search\">"                                                              ).append(crlf);
      searchMetawidget.append(""            ).append(""                                                                                                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t"    ).append(    "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchFirstName\" value=\"First Name:\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                                ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchFirstName\" value=\"#{customerBean.search.firstName}\"/>").append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchFirstName\" styleClass=\"error\"/>"                       ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchLastName\" value=\"Last Name:\"/>"                      ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                                ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchLastName\" value=\"#{customerBean.search.lastName}\"/>"  ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchLastName\" styleClass=\"error\"/>"                        ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t"    ).append(    "</h:panelGrid>"                                                                                  ).append(crlf);

      Assert.assertTrue(contents.contains(searchMetawidget));

      StringBuilder beanMetawidget = new StringBuilder();
      beanMetawidget.append("\t\t\t"      ).append("<h:dataTable id=\"customerBeanPageItems\" styleClass=\"data-table\" value=\"#{customerBean.pageItems}\" var=\"_item\">").append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "<h:column>"                                                                                                          ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<f:facet name=\"header\">"                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText value=\"First Name\"/>"                                                                            ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</f:facet>"                                                                                                        ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<h:link outcome=\"/customer/view\">"                                                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                                                    ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText id=\"itemFirstName\" value=\"#{_item.firstName}\"/>"                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</h:link>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "</h:column>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "<h:column>"                                                                                                          ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<f:facet name=\"header\">"                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText value=\"Last Name\"/>"                                                                             ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</f:facet>"                                                                                                        ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<h:link outcome=\"/customer/view\">"                                                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                                                    ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText id=\"itemLastName\" value=\"#{_item.lastName}\"/>"                                                 ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</h:link>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "</h:column>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t"      ).append("</h:dataTable>");

      Assert.assertTrue(contents.contains(beanMetawidget));

      FileResource<?> navigation = web.getWebResource("resources/scaffold/pageTemplate.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());

      StringBuilder navigationText = new StringBuilder();
      navigationText.append("\t\t\t\t"    ).append("<ul>"                                                         ).append(crlf);
      navigationText.append("\t\t\t\t\t"  ).append(  "<li>"                                                       ).append(crlf);
      navigationText.append("\t\t\t\t\t\t").append(    "<h:link outcome=\"/customer/search\" value=\"Customer\"/>").append(crlf);
      navigationText.append("\t\t\t\t\t"  ).append(  "</li>"                                                      ).append(crlf);
      navigationText.append("\t\t\t\t\t"  ).append(  "<li>"                                                       ).append(crlf);
      navigationText.append("\t\t\t\t\t\t").append(    "<h:link outcome=\"/grocery/search\" value=\"Grocery\"/>"  ).append(crlf);
      navigationText.append("\t\t\t\t\t"  ).append(  "</li>"                                                      ).append(crlf);

      Assert.assertTrue(contents.contains(navigationText));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testInsertRichFacesWidgetBuilder() throws Exception
   {
      // Note: this is not a very thorough test. Really we need a full integration test, so that we can run
      // 'richfaces setup'

      // In the middle

      CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget> existingWidgetBuilder = new CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget>(
               new CompositeWidgetBuilderConfig<StaticXmlWidget, StaticXmlMetawidget>().setWidgetBuilders(
                        new EntityWidgetBuilder(new EntityWidgetBuilderConfig()), new ReadOnlyWidgetBuilder(), new HtmlWidgetBuilder()));

      CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget> newWidgetBuilder = new FacesScaffold(null, null, null,
               null).insertRichFacesWidgetBuilder(existingWidgetBuilder);

      assertTrue(newWidgetBuilder.getWidgetBuilders()[0] instanceof EntityWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[1] instanceof ReadOnlyWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[2] instanceof RichFacesWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[3] instanceof HtmlWidgetBuilder);

      existingWidgetBuilder = new CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget>(
               new CompositeWidgetBuilderConfig<StaticXmlWidget, StaticXmlMetawidget>().setWidgetBuilders(
                        new ReadOnlyWidgetBuilder(), new HtmlWidgetBuilder()));

      newWidgetBuilder = new FacesScaffold(null, null, null, null).insertRichFacesWidgetBuilder(existingWidgetBuilder);

      assertTrue(newWidgetBuilder.getWidgetBuilders()[0] instanceof ReadOnlyWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[1] instanceof RichFacesWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[2] instanceof HtmlWidgetBuilder);

      // At the end

      existingWidgetBuilder = new CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget>(
               new CompositeWidgetBuilderConfig<StaticXmlWidget, StaticXmlMetawidget>().setWidgetBuilders(
                        new EntityWidgetBuilder(new EntityWidgetBuilderConfig()), new ReadOnlyWidgetBuilder()));

      newWidgetBuilder = new FacesScaffold(null, null, null, null).insertRichFacesWidgetBuilder(existingWidgetBuilder);

      assertTrue(newWidgetBuilder.getWidgetBuilders()[0] instanceof EntityWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[1] instanceof ReadOnlyWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[2] instanceof RichFacesWidgetBuilder);

      // At the start

      existingWidgetBuilder = new CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget>(
               new CompositeWidgetBuilderConfig<StaticXmlWidget, StaticXmlMetawidget>().setWidgetBuilders(
                        new EntityWidgetBuilder(new EntityWidgetBuilderConfig()), new HtmlWidgetBuilder()));

      newWidgetBuilder = new FacesScaffold(null, null, null, null).insertRichFacesWidgetBuilder(existingWidgetBuilder);

      assertTrue(newWidgetBuilder.getWidgetBuilders()[0] instanceof RichFacesWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[1] instanceof EntityWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[2] instanceof HtmlWidgetBuilder);

      // Already exists

      existingWidgetBuilder = new CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget>(
               new CompositeWidgetBuilderConfig<StaticXmlWidget, StaticXmlMetawidget>().setWidgetBuilders(
                        new EntityWidgetBuilder(new EntityWidgetBuilderConfig()), new RichFacesWidgetBuilder(), new HtmlWidgetBuilder()));

      newWidgetBuilder = new FacesScaffold(null, null, null, null).insertRichFacesWidgetBuilder(existingWidgetBuilder);

      assertTrue(newWidgetBuilder.getWidgetBuilders()[0] instanceof EntityWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[1] instanceof RichFacesWidgetBuilder);
      assertTrue(newWidgetBuilder.getWidgetBuilders()[2] instanceof HtmlWidgetBuilder);
   }

   @Test
   public void testGenerateEntityWithManyFields() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Item");
      getShell().execute("field string --named name");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named field1");
      getShell().execute("field string --named field2");
      getShell().execute("field oneToMany --named field3 --fieldType com.test.model.Item");
      getShell().execute("field string --named field4");
      getShell().execute("field string --named field5");
      getShell().execute("field string --named field6");
      getShell().execute("field string --named field7");
      getShell().execute("field string --named field8");
      queueInputLines("", "");
      getShell().execute("scaffold from-entity com.test.model.Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // Search

      FileResource<?> search = web.getWebResource("/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      String contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      StringBuilder searchMetawidget = new StringBuilder();
      searchMetawidget.append("\t\t"        ).append("<h:form id=\"search\">"                                                                        ).append(crlf);
      searchMetawidget.append("\t\t\t"      ).append(  "<h:panelGroup styleClass=\"search\">"                                                        ).append(crlf);
      searchMetawidget.append(""            ).append(""                                                                                              ).append(crlf);
      searchMetawidget.append("\t\t\t\t"    ).append(    "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchField1\" value=\"Field 1:\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchField1\" value=\"#{customerBean.search.field1}\"/>").append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchField1\" styleClass=\"error\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchField2\" value=\"Field 2:\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchField2\" value=\"#{customerBean.search.field2}\"/>").append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchField2\" styleClass=\"error\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchField4\" value=\"Field 4:\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchField4\" value=\"#{customerBean.search.field4}\"/>").append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchField4\" styleClass=\"error\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchField5\" value=\"Field 5:\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchField5\" value=\"#{customerBean.search.field5}\"/>").append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchField5\" styleClass=\"error\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputLabel for=\"customerBeanSearchField6\" value=\"Field 6:\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:panelGroup>"                                                                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:inputText id=\"customerBeanSearchField6\" value=\"#{customerBean.search.field6}\"/>").append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t").append(        "<h:message for=\"customerBeanSearchField6\" styleClass=\"error\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "</h:panelGroup>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"  ).append(      "<h:outputText/>"                                                                         ).append(crlf);
      searchMetawidget.append("\t\t\t\t"    ).append(    "</h:panelGrid>"                                                                            ).append(crlf);

      Assert.assertTrue(contents.contains(searchMetawidget));
   }

   @Test
   public void testGenerateFromNestedOneToOne() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Baz");
      getShell().execute("field string --named name");
      getShell().execute("entity --named Bar");
      getShell().execute("field string --named name");
      getShell().execute("field oneToOne --named baz --fieldType com.test.model.Baz");
      getShell().execute("entity --named Foo");
      getShell().execute("field string --named name");
      getShell().execute("field oneToOne --named bar --fieldType com.test.model.Bar");

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.model.*");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // Check create screen has 'Create New Profile'

      FileResource<?> create = web.getWebResource("/foo/create.xhtml");
      Assert.assertTrue(create.exists());

      getShell().execute("build");
   }

   @Test
   public void testGenerateEntityWithEnum() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("java new-enum-type --package com.test.model \"public enum RatingEnum{}\"");
      getShell().execute("java new-enum-const \"ONE_STAR\"");
      getShell().execute("java new-enum-const \"TWO_STAR\"");
      getShell().execute("java new-enum-const \"THREE_STAR\"");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      queueInputLines("com.test.model.RatingEnum");
      getShell().execute("field custom --named rating");
      queueInputLines("", "");
      getShell().execute("scaffold from-entity");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      StringBuilder metawidget = new StringBuilder();
      metawidget.append("\t"    ).append("<ui:define name=\"main\">"                                                                            ).append(crlf);
      metawidget.append("\t\t"  ).append(  "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                             ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                     ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>").append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText/>"                                                                                  ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                       ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"  ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText/>"                                                                                  ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputLabel for=\"customerBeanCustomerRating\" value=\"Rating:\"/>"                            ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText id=\"customerBeanCustomerRating\" value=\"#{customerBean.customer.rating}\"/>"      ).append(crlf);
      metawidget.append("\t\t\t").append(    "<h:outputText/>"                                                                                  ).append(crlf);
      metawidget.append("\t\t"  ).append(  "</h:panelGrid>"                                                                                     ).append(crlf);

      Assert.assertTrue(contents.contains(metawidget));

      // Create

      FileResource<?> create = web.getWebResource("/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      metawidget = new StringBuilder();
      metawidget.append("\t\t"        ).append("<h:form id=\"create\">"                                                                                ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:messages globalOnly=\"true\" styleClass=\"error\"/>"                                              ).append(crlf);
      metawidget.append(""            ).append(""                                                                                                      ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                              ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>"                      ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:panelGroup>"                                                                                    ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>").append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>"                         ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "</h:panelGroup>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputText/>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>"                        ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:panelGroup>"                                                                                    ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>"  ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>"                          ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "</h:panelGroup>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputText/>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputLabel for=\"customerBeanCustomerRating\" value=\"Rating:\"/>"                             ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:panelGroup>"                                                                                    ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:selectOneMenu id=\"customerBeanCustomerRating\" value=\"#{customerBean.customer.rating}\">"   ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<f:selectItem/>"                                                                               ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<f:selectItem itemValue=\"ONE_STAR\"/>"                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<f:selectItem itemValue=\"TWO_STAR\"/>"                                                        ).append(crlf);
      metawidget.append("\t\t\t\t\t\t").append(        "<f:selectItem itemValue=\"THREE_STAR\"/>"                                                      ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "</h:selectOneMenu>"                                                                              ).append(crlf);
      metawidget.append("\t\t\t\t\t"  ).append(      "<h:message for=\"customerBeanCustomerRating\" styleClass=\"error\"/>"                            ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "</h:panelGroup>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t\t"    ).append(    "<h:outputText/>"                                                                                   ).append(crlf);
      metawidget.append("\t\t\t"      ).append(  "</h:panelGrid>"                                                                                      ).append(crlf);

      Assert.assertTrue(contents.contains(metawidget));

      // Search

      FileResource<?> search = web.getWebResource("/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      StringBuilder searchMetawidget = new StringBuilder();
      searchMetawidget.append("\t\t"          ).append("<h:form id=\"search\">"                                                                              ).append(crlf);
      searchMetawidget.append("\t\t\t"        ).append(  "<h:panelGroup styleClass=\"search\">"                                                              ).append(crlf);
      searchMetawidget.append(""              ).append(""                                                                                                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t"      ).append(    "<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">"                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:outputLabel for=\"customerBeanSearchFirstName\" value=\"First Name:\"/>"                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:panelGroup>"                                                                                ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t"  ).append(        "<h:inputText id=\"customerBeanSearchFirstName\" value=\"#{customerBean.search.firstName}\"/>").append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t"  ).append(        "<h:message for=\"customerBeanSearchFirstName\" styleClass=\"error\"/>"                       ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "</h:panelGroup>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:outputText/>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:outputLabel for=\"customerBeanSearchLastName\" value=\"Last Name:\"/>"                      ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:panelGroup>"                                                                                ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t"  ).append(        "<h:inputText id=\"customerBeanSearchLastName\" value=\"#{customerBean.search.lastName}\"/>"  ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t"  ).append(        "<h:message for=\"customerBeanSearchLastName\" styleClass=\"error\"/>"                        ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "</h:panelGroup>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:outputText/>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:outputLabel for=\"customerBeanSearchRating\" value=\"Rating:\"/>"                           ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:panelGroup>"                                                                                ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t"  ).append(        "<h:selectOneMenu id=\"customerBeanSearchRating\" value=\"#{customerBean.search.rating}\">"   ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t\t").append(          "<f:selectItem/>"                                                                           ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t\t").append(          "<f:selectItem itemValue=\"ONE_STAR\"/>"                                                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t\t").append(          "<f:selectItem itemValue=\"TWO_STAR\"/>"                                                    ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t\t").append(          "<f:selectItem itemValue=\"THREE_STAR\"/>"                                                  ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t"  ).append(        "</h:selectOneMenu>"                                                                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t\t"  ).append(        "<h:message for=\"customerBeanSearchRating\" styleClass=\"error\"/>"                          ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "</h:panelGroup>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t\t"    ).append(      "<h:outputText/>"                                                                               ).append(crlf);
      searchMetawidget.append("\t\t\t\t"      ).append(    "</h:panelGrid>"                                                                                  ).append(crlf);

      Assert.assertTrue(contents.contains(searchMetawidget));

      StringBuilder beanMetawidget = new StringBuilder();
      beanMetawidget.append("\t\t\t"      ).append("<h:dataTable id=\"customerBeanPageItems\" styleClass=\"data-table\" value=\"#{customerBean.pageItems}\" var=\"_item\">").append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "<h:column>"                                                                                                          ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<f:facet name=\"header\">"                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText value=\"First Name\"/>"                                                                            ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</f:facet>"                                                                                                        ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<h:link outcome=\"/customer/view\">"                                                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                                                    ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText id=\"itemFirstName\" value=\"#{_item.firstName}\"/>"                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</h:link>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "</h:column>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "<h:column>"                                                                                                          ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<f:facet name=\"header\">"                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText value=\"Last Name\"/>"                                                                             ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</f:facet>"                                                                                                        ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<h:link outcome=\"/customer/view\">"                                                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                                                    ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText id=\"itemLastName\" value=\"#{_item.lastName}\"/>"                                                 ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</h:link>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "</h:column>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "<h:column>"                                                                                                          ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<f:facet name=\"header\">"                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText value=\"Rating\"/>"                                                                                ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</f:facet>"                                                                                                        ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "<h:link outcome=\"/customer/view\">"                                                                               ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<f:param name=\"id\" value=\"#{_item.id}\"/>"                                                                    ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t\t").append(      "<h:outputText id=\"itemRating\" value=\"#{_item.rating}\"/>"                                                     ).append(crlf);
      beanMetawidget.append("\t\t\t\t\t"  ).append(    "</h:link>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t\t"    ).append(  "</h:column>"                                                                                                         ).append(crlf);
      beanMetawidget.append("\t\t\t"      ).append("</h:dataTable>");

      Assert.assertTrue(contents.contains(beanMetawidget));

      // Backing Bean

      FileResource<?> customerBean = java.getJavaResource("/com/test/view/CustomerBean.java");
      Assert.assertTrue(customerBean.exists());
      contents = Streams.toString(customerBean.getResourceInputStream());

      Assert.assertTrue(contents.contains("\n\tprivate Customer customer;"));

      StringBuilder qbeMetawidget = new StringBuilder();
      qbeMetawidget.append("\t\t"  ).append("List<Predicate> predicatesList = new ArrayList<Predicate>();"                               ).append(crlf);
      qbeMetawidget.append(""      ).append(""                                                                                           ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("String firstName = this.search.getFirstName();"                                             ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("if (firstName != null && !\"\".equals(firstName)) {"                                        ).append(crlf);
      qbeMetawidget.append("\t\t\t").append(  "predicatesList.add(builder.like(root.<String>get(\"firstName\"), '%' + firstName + '%'));").append(crlf);
      qbeMetawidget.append("\t\t"  ).append("}"                                                                                          ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("String lastName = this.search.getLastName();"                                               ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("if (lastName != null && !\"\".equals(lastName)) {"                                          ).append(crlf);
      qbeMetawidget.append("\t\t\t").append(  "predicatesList.add(builder.like(root.<String>get(\"lastName\"), '%' + lastName + '%'));"  ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("}"                                                                                          ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("RatingEnum rating = this.search.getRating();"                                               ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("if (rating != null) {"                                                                      ).append(crlf);
      qbeMetawidget.append("\t\t\t").append(  "predicatesList.add(builder.equal(root.get(\"rating\"), rating));"                         ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("}"                                                                                          ).append(crlf);
      qbeMetawidget.append(""       ).append(""                                                                                          ).append(crlf);
      qbeMetawidget.append("\t\t"  ).append("return ");

      Assert.assertTrue(contents.contains(qbeMetawidget));

      Assert.assertTrue(contents.contains("\n\tprivate Customer add = new Customer();"));
      Assert.assertTrue(contents.contains("\n\t\tthis.add = new Customer();"));

      // ViewUtils

      FileResource<?> viewUtils = java.getJavaResource("/com/test/view/ViewUtils.java");
      Assert.assertTrue(viewUtils.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains("template=\"/resources/scaffold/pageTemplate.xhtml"));

      FileResource<?> taglib = web.getWebResource("WEB-INF/classes/META-INF/forge.taglib.xml");
      Assert.assertTrue(taglib.exists());
      contents = Streams.toString(taglib.getResourceInputStream());
      Assert.assertTrue(contents.contains("<function-class>com.test.view.ViewUtils</function-class>"));

      // Additional files

      Assert.assertTrue(web.getWebResource("resources/add.png").exists());
      Assert.assertTrue(web.getWebResource("resources/background.gif").exists());
      Assert.assertTrue(web.getWebResource("resources/false.png").exists());
      Assert.assertTrue(web.getWebResource("resources/favicon.ico").exists());
      Assert.assertTrue(web.getWebResource("resources/forge-logo.png").exists());

      FileResource<?> css = web.getWebResource("resources/forge-style.css");
      Assert.assertTrue(css.exists());
      contents = Streams.toString(css.getResourceInputStream());
      Assert.assertTrue(contents.contains("#content .error {"));

      Assert.assertTrue(web.getWebResource("resources/jboss-community.png").exists());
      Assert.assertTrue(web.getWebResource("resources/remove.png").exists());
      Assert.assertTrue(web.getWebResource("resources/search.png").exists());
      Assert.assertTrue(web.getWebResource("resources/true.png").exists());
      Assert.assertTrue(web.getWebResource("resources/scaffold/pageTemplate.xhtml").exists());

      FileResource<?> paginator = web.getWebResource("resources/scaffold/paginator.xhtml");
      Assert.assertTrue(paginator.exists());
      contents = Streams.toString(paginator.getResourceInputStream());

      // Paginator should use commandLink, not outputLink, else search criteria gets lost on page change

      Assert.assertTrue(contents.contains("<h:commandLink "));
      Assert.assertTrue(!contents.contains("<h:outputLink "));
   }
}
