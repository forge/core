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
import org.jboss.forge.shell.exceptions.PluginExecutionException;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class FacesScaffoldTest extends AbstractShellTest
{
   @Test
   public void testScaffoldSetup() throws Exception
   {
      Project project = setupScaffoldProject();
      ServletFacet servlet = project.getFacet(ServletFacet.class);

      Assert.assertTrue(project.hasFacet(FacesScaffold.class));

      Node root = XMLParser.parse(servlet.getConfigFile().getResourceInputStream());
      List<Node> errorPages = root.get("error-page");
      Assert.assertEquals("/404.jsf", errorPages.get(0).getSingle("location").getText());
      Assert.assertEquals("/500.jsf", errorPages.get(1).getSingle("location").getText());

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      FileResource<?> e404 = web.getWebResource("404.xhtml");
      Assert.assertTrue(Streams.toString(e404.getResourceInputStream()).contains(
               "/resources/scaffold/page.xhtml"));

      // Test page exists, but has no navigation

      FileResource<?> page = web.getWebResource("/resources/scaffold/page.xhtml");
      Assert.assertTrue(page.exists());
      String contents = Streams.toString(page.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "<div class=\"wrapper\">"));
      Assert.assertTrue(!contents.contains(
               "<h:link outcome=\"/scaffold>"));
   }

   @Test(expected = PluginExecutionException.class)
   public void testCannotGenerateFromEntityUntilScaffoldInstalled() throws Exception
   {
      initializeJavaProject();

      queueInputLines("");
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

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      StringBuilder metawidget = new StringBuilder("\t\t<h:form id=\"form\">\n");
      metawidget.append("\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>\n");

      Assert.assertTrue(contents.contains(metawidget));

      // Create

      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      metawidget = new StringBuilder("\t\t<h:form id=\"form\">\n");
      metawidget.append("\t\t\t<h:messages globalOnly=\"true\"/>\n\n");
      metawidget.append("\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerFirstName\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerLastName\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>\n");

      Assert.assertTrue(contents.contains(metawidget));

      // List

      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");
      Assert.assertTrue(list.exists());
      contents = Streams.toString(list.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      metawidget = new StringBuilder("<ui:define name=\"main\">\r\n");
      metawidget
               .append("\t\t<h:dataTable id=\"customerBeanPageItems\" value=\"#{customerBean.pageItems}\" var=\"_item\">\r\n");
      metawidget.append("\t\t\t<h:column>\r\n");
      metawidget.append("\t\t\t\t<f:facet name=\"header\">\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText value=\"First Name\"/>\r\n");
      metawidget.append("\t\t\t\t</f:facet>\r\n");
      metawidget.append("\t\t\t\t<h:link outcome=\"/scaffold/customer/view\" value=\"#{_item.firstName}\">\r\n");
      metawidget.append("\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      metawidget.append("\t\t\t\t</h:link>\r\n");
      metawidget.append("\t\t\t</h:column>\r\n");
      metawidget.append("\t\t\t<h:column>\r\n");
      metawidget.append("\t\t\t\t<f:facet name=\"header\">\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText value=\"Last Name\"/>\r\n");
      metawidget.append("\t\t\t\t</f:facet>\r\n");
      metawidget.append("\t\t\t\t<h:link outcome=\"/scaffold/customer/view\" value=\"#{_item.lastName}\">\r\n");
      metawidget.append("\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      metawidget.append("\t\t\t\t</h:link>\r\n");
      metawidget.append("\t\t\t</h:column>\r\n");
      metawidget.append("\t\t</h:dataTable>");

      Assert.assertTrue(contents.contains(metawidget));

      // Additional files

      FileResource<?> navigation = web.getWebResource("resources/scaffold/page.xhtml");
      Assert.assertTrue(navigation.exists());

      FileResource<?> paginator = web.getWebResource("resources/scaffold/paginator.xhtml");
      Assert.assertTrue(paginator.exists());

      FileResource<?> viewUtils = java.getJavaResource("/com/test/view/ViewUtils.java");
      Assert.assertTrue(viewUtils.exists());

      FileResource<?> taglib = web.getWebResource("WEB-INF/classes/META-INF/forge.taglib.xml");
      Assert.assertTrue(taglib.exists());
      contents = Streams.toString(taglib.getResourceInputStream());
      Assert.assertTrue(contents.contains("<function-class>com.test.view.ViewUtils</function-class>"));
   }

   @Test
   public void testGenerateFromEntityCamelCase() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named CustomerPerson");
      getShell().execute("field string --named name");

      queueInputLines("", "");
      getShell().execute("scaffold from-entity");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("scaffold/customerPerson/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());

      Assert.assertTrue(contents.contains("<ui:param name=\"pageTitle\" value=\"View Customer Person\"/>"));
      Assert.assertTrue(contents.contains("\t<ui:define name=\"header\">\n\t\tCustomer Person\n\t</ui:define>"));
      Assert.assertTrue(contents
               .contains("\t<ui:define name=\"subheader\">\n\t\tViewing the Customer Person\n\t</ui:define>"));
      Assert.assertTrue(contents.contains(
               "customerPersonBean.customerPerson"));

      // Create

      FileResource<?> create = web.getWebResource("scaffold/customerPerson/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());

      Assert.assertTrue(contents.contains("<ui:param name=\"pageTitle\" value=\"Create Customer Person\"/>"));
      Assert.assertTrue(contents.contains("\t<ui:define name=\"header\">\n\t\tCustomer Person\n\t</ui:define>"));
      Assert.assertTrue(contents.contains("Editing existing Customer Person"));
      Assert.assertTrue(contents.contains("Creating a new Customer Person"));
      Assert.assertTrue(contents.contains(
               "customerPersonBean.customerPerson"));

      // List

      FileResource<?> list = web.getWebResource("scaffold/customerPerson/list.xhtml");
      Assert.assertTrue(list.exists());
      contents = Streams.toString(list.getResourceInputStream()).replaceAll("\\r\\n", "\n");

      Assert.assertTrue(contents.contains("<ui:param name=\"pageTitle\" value=\"List all Customer Person entities\"/>"));
      Assert.assertTrue(contents.contains("\t<ui:define name=\"header\">\n\t\tCustomer Person\n\t</ui:define>"));
      Assert.assertTrue(contents
               .contains("\t<ui:define name=\"subheader\">\n\t\tListing all Customer Person entities\n\t</ui:define>"));
      Assert.assertTrue(contents.contains(
               "customerPersonBean.pageItems"));

      // Navigation

      FileResource<?> navigation = web.getWebResource("resources/scaffold/page.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());
      Assert.assertTrue(contents
               .contains("<h:link outcome=\"/scaffold/customerPerson/list\" value=\"Customer Person\"/>"));
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

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");

      for (FileResource<?> file : Arrays.asList(view, create, list))
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
         FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");

         for (FileResource<?> file : Arrays.asList(view, create, list))
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
      queueInputLines("com.test.domain.Address");
      getShell().execute("field custom --named address");

      queueInputLines("", "");
      getShell().execute("scaffold from-entity");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      // Create

      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));
      StringBuilder metawidget = new StringBuilder("\t\t<h:form id=\"form\">\n");
      metawidget.append("\t\t\t<h:messages globalOnly=\"true\"/>\n\n");
      metawidget.append("\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerFirstName\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerLastName\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerAddress\" value=\"Address:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\" id=\"customerBeanCustomerAddress\">\r\n");
      metawidget
               .append("\t\t\t\t\t<h:outputLabel for=\"customerBeanCustomerAddressStreet\" value=\"Street:\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanCustomerAddressStreet\" value=\"#{customerBean.customer.address.street}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanCustomerAddressStreet\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanCustomerAddressCity\" value=\"City:\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanCustomerAddressCity\" value=\"#{customerBean.customer.address.city}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanCustomerAddressCity\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanCustomerAddressState\" value=\"State:\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanCustomerAddressState\" value=\"#{customerBean.customer.address.state}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanCustomerAddressState\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanCustomerAddressZip\" value=\"Zip:\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanCustomerAddressZip\" value=\"#{customerBean.customer.address.zip}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanCustomerAddressZip\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGrid>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>\n");
      Assert.assertTrue(contents.contains(metawidget));

      // List

      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");
      Assert.assertTrue(list.exists());
      contents = Streams.toString(list.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

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
      getShell().execute("field manyToOne --named employer --fieldType com.test.domain.Employer");

      // (need to specify both entities until https://issues.jboss.org/browse/FORGE-392)

      queueInputLines("", "");
      getShell().execute("scaffold from-entity com.test.domain.Employer com.test.domain.Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      StringBuilder metawidget = new StringBuilder(
               "\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerEmployer\" value=\"Employer:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:link converter=\"#{employerBean.converter}\" id=\"customerBeanCustomerEmployer\" outcome=\"/scaffold/employer/view\" value=\"#{customerBean.customer.employer}\">\r\n");
      metawidget.append("\t\t\t\t\t<f:param name=\"id\" value=\"#{customerBean.customer.employer.id}\"/>\r\n");
      metawidget.append("\t\t\t\t</h:link>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));

      // Create

      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      metawidget = new StringBuilder("\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerFirstName\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerLastName\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerEmployer\" value=\"Employer:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:selectOneMenu converter=\"#{employerBean.converter}\" id=\"customerBeanCustomerEmployer\" value=\"#{customerBean.customer.employer}\">\r\n");
      metawidget.append("\t\t\t\t\t\t<f:selectItem/>\r\n");
      metawidget.append("\t\t\t\t\t\t<f:selectItems value=\"#{employerBean.all}\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:selectOneMenu>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerEmployer\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));

      // List

      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");
      Assert.assertTrue(list.exists());
      contents = Streams.toString(list.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      // Navigation

      FileResource<?> navigation = web.getWebResource("resources/scaffold/page.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());

      StringBuilder navigationText = new StringBuilder("\n\t\t\t\t<ul>\r\n");
      navigationText.append("\t\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/customer/list\" value=\"Customer\"/>\r\n");
      navigationText.append("\t\t\t\t\t</li>\r\n");
      navigationText.append("\t\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/employer/list\" value=\"Employer\"/>\r\n");
      navigationText.append("\t\t\t\t\t</li>\r\n");

      Assert.assertTrue(contents.contains(navigationText));
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
      getShell().execute("field oneToMany --named groceries --fieldType com.test.domain.Grocery");

      // (need to specify both entities until https://issues.jboss.org/browse/FORGE-392)

      queueInputLines("", "");
      getShell().execute("scaffold from-entity com.test.domain.Grocery com.test.domain.Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      StringBuilder metawidget = new StringBuilder(
               "\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerGroceries\" value=\"Groceries:\"/>\r\n");
      metawidget
               .append("\t\t\t\t<h:dataTable id=\"customerBeanCustomerGroceries\" value=\"#{forgeview:asList(customerBean.customer.groceries)}\" var=\"_item\">\r\n");
      metawidget.append("\t\t\t\t\t<h:column>\r\n");
      metawidget.append("\t\t\t\t\t\t<f:facet name=\"header\">\r\n");
      metawidget.append("\t\t\t\t\t\t\t<h:outputText value=\"Name\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t</f:facet>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/grocery/view\" value=\"#{_item.name}\">\r\n");
      metawidget.append("\t\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t</h:link>\r\n");
      metawidget.append("\t\t\t\t\t</h:column>\r\n");
      metawidget.append("\t\t\t\t</h:dataTable>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));

      // Create

      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      metawidget = new StringBuilder("\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerFirstName\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerLastName\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel value=\"Groceries:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<ui:param name=\"_collection\" value=\"#{customerBean.customer.groceries}\"/>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:dataTable id=\"customerBeanCustomerGroceries\" styleClass=\"datatable\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">\r\n");
      metawidget.append("\t\t\t\t\t\t<h:column>\r\n");
      metawidget.append("\t\t\t\t\t\t\t<f:facet name=\"header\">\r\n");
      metawidget.append("\t\t\t\t\t\t\t\t<h:outputText value=\"Name\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t\t</f:facet>\r\n");
      metawidget.append("\t\t\t\t\t\t\t<h:link outcome=\"/scaffold/grocery/view\" value=\"#{_item.name}\">\r\n");
      metawidget.append("\t\t\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t\t</h:link>\r\n");
      metawidget.append("\t\t\t\t\t\t</h:column>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:column>\r\n");
      metawidget.append("\t\t\t\t\t\t\t<h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"button\" value=\"Remove\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t</h:column>\r\n");
      metawidget.append("\t\t\t\t\t</h:dataTable>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:selectOneMenu converter=\"#{groceryBean.converter}\" value=\"#{requestScope['customerBeanCustomerGroceriesAdd']}\">\r\n");
      metawidget.append("\t\t\t\t\t\t<f:selectItem/>\r\n");
      metawidget.append("\t\t\t\t\t\t<f:selectItems value=\"#{groceryBean.all}\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:selectOneMenu>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:commandLink action=\"#{_collection.add(requestScope['customerBeanCustomerGroceriesAdd'])}\" styleClass=\"button\" value=\"Add\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));

      // List

      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");
      Assert.assertTrue(list.exists());
      contents = Streams.toString(list.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      FileResource<?> navigation = web.getWebResource("resources/scaffold/page.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());

      StringBuilder navigationText = new StringBuilder("\n\t\t\t\t<ul>\r\n");
      navigationText.append("\t\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/customer/list\" value=\"Customer\"/>\r\n");
      navigationText.append("\t\t\t\t\t</li>\r\n");
      navigationText.append("\t\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/grocery/list\" value=\"Grocery\"/>\r\n");
      navigationText.append("\t\t\t\t\t</li>\r\n");

      Assert.assertTrue(contents.contains(navigationText));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGenerateFromEntityWithRichFaces() throws Exception
   {
      Project project = setupScaffoldProject();
      // getShell().execute("richfaces setup");

      queueInputLines("");
      getShell().execute("entity --named Customer");
      queueInputLines("java.util.Date");
      getShell().execute("field custom --named dateJoined");
      queueInputLines("java.awt.Color");
      getShell().execute("field custom --named favoriteColor");

      queueInputLines("", "");
      getShell().execute("scaffold from-entity");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");

      for (FileResource<?> file : Arrays.asList(view, create, list))
      {
         Assert.assertTrue(file.exists());
         String contents = Streams.toString(file.getResourceInputStream());
         Assert.assertTrue(contents.contains(
                  "template=\"/resources/scaffold/page.xhtml"));
      }
   }

   public Project setupScaffoldProject() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "", "2", "", "", "");
      getShell().execute("scaffold setup");
      return project;
   }
}
