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
      Assert.assertEquals("/faces/error.xhtml", errorPages.get(0).getSingle("location").getText());

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      FileResource<?> error = web.getWebResource("error.xhtml");
      Assert.assertTrue(Streams.toString(error.getResourceInputStream()).contains(
               "/resources/scaffold/page.xhtml"));

      // Test page exists, but has no navigation

      FileResource<?> page = web.getWebResource("/resources/scaffold/page.xhtml");
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

      StringBuilder metawidget = new StringBuilder("<ui:define name=\"main\">\n");
      metawidget.append("\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget
               .append("\t\t\t<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget
               .append("\t\t\t<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t</h:panelGrid>\n");

      Assert.assertTrue(contents.contains(metawidget));

      // Create

      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      metawidget = new StringBuilder("<h:form id=\"create\">\n");
      metawidget.append("\t\t\t<h:messages globalOnly=\"true\"/>\n\n");
      metawidget.append("\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>\n");

      Assert.assertTrue(contents.contains(metawidget));

      // Search

      FileResource<?> search = web.getWebResource("scaffold/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      StringBuilder searchMetawidget = new StringBuilder("<h:form id=\"search\">\r\n");
      searchMetawidget.append("\t\t\t<h:panelGroup styleClass=\"search\">\r\n");
      searchMetawidget.append("\t\t\t\t<h:messages globalOnly=\"true\"/>\r\n\r\n");
      searchMetawidget.append("\t\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchFirstName\" value=\"First Name:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchFirstName\" value=\"#{customerBean.search.firstName}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchFirstName\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchLastName\" value=\"Last Name:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchLastName\" value=\"#{customerBean.search.lastName}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchLastName\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t</h:panelGrid>\r\n");

      Assert.assertTrue(contents.contains(searchMetawidget));

      StringBuilder beanMetawidget = new StringBuilder("\n\t\t\t<h:dataTable id=\"customerBeanPageItems\" styleClass=\"data-table\" value=\"#{customerBean.pageItems}\" var=\"_item\">\r\n");
      beanMetawidget.append("\t\t\t\t<h:column>\r\n");
      beanMetawidget.append("\t\t\t\t\t<f:facet name=\"header\">\r\n");
      beanMetawidget.append("\t\t\t\t\t\t<h:outputText value=\"First Name\"/>\r\n");
      beanMetawidget.append("\t\t\t\t\t</f:facet>\r\n");
      beanMetawidget.append("\t\t\t\t\t<h:link outcome=\"/scaffold/customer/view\" value=\"#{_item.firstName}\">\r\n");
      beanMetawidget.append("\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      beanMetawidget.append("\t\t\t\t\t</h:link>\r\n");
      beanMetawidget.append("\t\t\t\t</h:column>\r\n");
      beanMetawidget.append("\t\t\t\t<h:column>\r\n");
      beanMetawidget.append("\t\t\t\t\t<f:facet name=\"header\">\r\n");
      beanMetawidget.append("\t\t\t\t\t\t<h:outputText value=\"Last Name\"/>\r\n");
      beanMetawidget.append("\t\t\t\t\t</f:facet>\r\n");
      beanMetawidget.append("\t\t\t\t\t<h:link outcome=\"/scaffold/customer/view\" value=\"#{_item.lastName}\">\r\n");
      beanMetawidget.append("\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      beanMetawidget.append("\t\t\t\t\t</h:link>\r\n");
      beanMetawidget.append("\t\t\t\t</h:column>\r\n");
      beanMetawidget.append("\t\t\t</h:dataTable>");

      Assert.assertTrue(contents.contains(beanMetawidget));

      // Backing Bean

      FileResource<?> customerBean = java.getJavaResource("/com/test/view/CustomerBean.java");
      Assert.assertTrue(customerBean.exists());
      contents = Streams.toString(customerBean.getResourceInputStream());

      Assert.assertTrue(contents.contains("\n\tprivate Customer customer;"));

      StringBuilder qbeMetawidget = new StringBuilder(
               "List<Predicate> predicatesList = new ArrayList<Predicate>();\r\n\r\n");
      qbeMetawidget.append("\t\tString firstName = this.search.getFirstName();\r\n");
      qbeMetawidget.append("\t\tif (firstName != null && !\"\".equals(firstName)) {\r\n");
      qbeMetawidget
               .append("\t\t\tpredicatesList.add(builder.like(root.<String>get(\"firstName\"), '%' + firstName + '%'));\r\n");
      qbeMetawidget.append("\t\t}\r\n");
      qbeMetawidget.append("\t\tString lastName = this.search.getLastName();\r\n");
      qbeMetawidget.append("\t\tif (lastName != null && !\"\".equals(lastName)) {\r\n");
      qbeMetawidget
               .append("\t\t\tpredicatesList.add(builder.like(root.<String>get(\"lastName\"), '%' + lastName + '%'));\r\n");
      qbeMetawidget.append("\t\t}\r\n\r\n");
      qbeMetawidget.append("\t\treturn ");

      Assert.assertTrue(contents.contains(qbeMetawidget));

      // ViewUtils

      FileResource<?> viewUtils = java.getJavaResource("/com/test/view/ViewUtils.java");
      Assert.assertTrue(viewUtils.exists());
      contents = Streams.toString(create.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      FileResource<?> taglib = web.getWebResource("WEB-INF/classes/META-INF/forge.taglib.xml");
      Assert.assertTrue(taglib.exists());
      contents = Streams.toString(taglib.getResourceInputStream());
      Assert.assertTrue(contents.contains("<function-class>com.test.view.ViewUtils</function-class>"));

      // Additional files

      Assert.assertTrue(web.getWebResource("resources/background.gif").exists());
      Assert.assertTrue(web.getWebResource("resources/favicon.ico").exists());
      Assert.assertTrue(web.getWebResource("resources/forge-logo.png").exists());

      FileResource<?> css = web.getWebResource("resources/forge-style.css");
      Assert.assertTrue(css.exists());
      contents = Streams.toString(css.getResourceInputStream());
      Assert.assertTrue(contents.contains("#content table .component .error {"));

      Assert.assertTrue(web.getWebResource("resources/jboss-community.png").exists());
      Assert.assertTrue(web.getWebResource("resources/search.png").exists());
      Assert.assertTrue(web.getWebResource("resources/scaffold/page.xhtml").exists());

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
               .contains("\t<ui:define name=\"subheader\">\n\t\tView existing Customer Person\n\t</ui:define>"));
      Assert.assertTrue(contents.contains(
               "customerPersonBean.customerPerson"));

      // Create

      FileResource<?> create = web.getWebResource("scaffold/customerPerson/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());

      Assert.assertTrue(contents.contains("<ui:param name=\"pageTitle\" value=\"Create Customer Person\"/>"));
      Assert.assertTrue(contents.contains("\t<ui:define name=\"header\">\n\t\tCustomer Person\n\t</ui:define>"));
      Assert.assertTrue(contents.contains("Edit existing Customer Person"));
      Assert.assertTrue(contents.contains("Create a new Customer Person"));
      Assert.assertTrue(contents.contains(
               "customerPersonBean.customerPerson"));

      // Search

      FileResource<?> search = web.getWebResource("scaffold/customerPerson/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream()).replaceAll("\\r\\n", "\n");

      Assert.assertTrue(contents.contains("<ui:param name=\"pageTitle\" value=\"Search Customer Person entities\"/>"));
      Assert.assertTrue(contents.contains("\t<ui:define name=\"header\">\n\t\tCustomer Person\n\t</ui:define>"));
      Assert.assertTrue(contents
               .contains("\t<ui:define name=\"subheader\">\n\t\tSearch Customer Person entities\n\t</ui:define>"));
      Assert.assertTrue(contents.contains(
               "customerPersonBean.pageItems"));

      // Navigation

      FileResource<?> navigation = web.getWebResource("resources/scaffold/page.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());
      Assert.assertTrue(contents
               .contains("<h:link outcome=\"/scaffold/customerPerson/search\" value=\"Customer Person\"/>"));
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
      FileResource<?> search = web.getWebResource("scaffold/customer/search.xhtml");

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
      StringBuilder metawidget = new StringBuilder("\t\t<h:form id=\"create\">\n");
      metawidget.append("\t\t\t<h:messages globalOnly=\"true\"/>\n\n");
      metawidget.append("\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>\r\n");
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
      metawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanCustomerAddressStreet\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanCustomerAddressCity\" value=\"City:\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanCustomerAddressCity\" value=\"#{customerBean.customer.address.city}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanCustomerAddressCity\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanCustomerAddressState\" value=\"State:\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanCustomerAddressState\" value=\"#{customerBean.customer.address.state}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanCustomerAddressState\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanCustomerAddressZip\" value=\"Zip:\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanCustomerAddressZip\" value=\"#{customerBean.customer.address.zip}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanCustomerAddressZip\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGrid>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>\n");
      Assert.assertTrue(contents.contains(metawidget));

      // Search

      FileResource<?> search = web.getWebResource("scaffold/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
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

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      StringBuilder metawidget = new StringBuilder(
               "\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget
               .append("\t\t\t<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget
               .append("\t\t\t<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t<h:outputLabel for=\"customerBeanCustomerEmployer\" value=\"Employer:\"/>\r\n");
      metawidget
               .append("\t\t\t<h:link converter=\"#{employerBean.converter}\" id=\"customerBeanCustomerEmployer\" outcome=\"/scaffold/employer/view\" value=\"#{customerBean.customer.employer}\">\r\n");
      metawidget.append("\t\t\t\t<f:param name=\"id\" value=\"#{customerBean.customer.employer.id}\"/>\r\n");
      metawidget.append("\t\t\t</h:link>\r\n");
      metawidget.append("\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t</h:panelGrid>");

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
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerEmployer\" value=\"Employer:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:selectOneMenu converter=\"#{employerBean.converter}\" id=\"customerBeanCustomerEmployer\" value=\"#{customerBean.customer.employer}\">\r\n");
      metawidget.append("\t\t\t\t\t\t<f:selectItem/>\r\n");
      metawidget.append("\t\t\t\t\t\t<f:selectItems value=\"#{employerBean.all}\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:selectOneMenu>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerEmployer\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));

      // Search

      FileResource<?> search = web.getWebResource("scaffold/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      // Navigation

      FileResource<?> navigation = web.getWebResource("resources/scaffold/page.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());

      StringBuilder navigationText = new StringBuilder("\n\t\t\t\t<ul>\r\n");
      navigationText.append("\t\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/customer/search\" value=\"Customer\"/>\r\n");
      navigationText.append("\t\t\t\t\t</li>\r\n");
      navigationText.append("\t\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/employer/search\" value=\"Employer\"/>\r\n");
      navigationText.append("\t\t\t\t\t</li>\r\n");

      Assert.assertTrue(contents.contains(navigationText));

      // Backing Bean

      FileResource<?> customerBean = java.getJavaResource("/com/test/view/CustomerBean.java");
      Assert.assertTrue(customerBean.exists());
      contents = Streams.toString(customerBean.getResourceInputStream());

      StringBuilder qbeMetawidget = new StringBuilder("\t\tEmployer employer = this.search.getEmployer();\r\n");
      qbeMetawidget.append("\t\tif (employer != null && employer.getId() != null) {\r\n");
      qbeMetawidget
               .append("\t\t\tpredicatesList.add(builder.equal(root.get(\"employer\"),employer));\r\n");
      qbeMetawidget.append("\t\t}\r\n");

      Assert.assertTrue(contents.contains(qbeMetawidget));
      Assert.assertTrue(contents.contains("import com.test.domain.Customer;\r\nimport com.test.domain.Employer;\r\n"));
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
               "\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      metawidget.append("\t\t\t<h:outputLabel for=\"customerBeanCustomerFirstName\" value=\"First Name:\"/>\r\n");
      metawidget
               .append("\t\t\t<h:outputText id=\"customerBeanCustomerFirstName\" value=\"#{customerBean.customer.firstName}\"/>\r\n");
      metawidget.append("\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget
               .append("\t\t\t<h:outputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t<h:outputLabel for=\"customerBeanCustomerGroceries\" value=\"Groceries:\"/>\r\n");
      metawidget
               .append("\t\t\t<h:dataTable id=\"customerBeanCustomerGroceries\" styleClass=\"data-table\" value=\"#{forgeview:asList(customerBean.customer.groceries)}\" var=\"_item\">\r\n");
      metawidget.append("\t\t\t\t<h:column>\r\n");
      metawidget.append("\t\t\t\t\t<f:facet name=\"header\">\r\n");
      metawidget.append("\t\t\t\t\t\t<h:outputText value=\"Name\"/>\r\n");
      metawidget.append("\t\t\t\t\t</f:facet>\r\n");
      metawidget.append("\t\t\t\t\t<h:link outcome=\"/scaffold/grocery/view\" value=\"#{_item.name}\">\r\n");
      metawidget.append("\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:link>\r\n");
      metawidget.append("\t\t\t\t</h:column>\r\n");
      metawidget.append("\t\t\t</h:dataTable>\r\n");
      metawidget.append("\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));
      Assert.assertTrue(contents.contains("xmlns:forgeview=\"http://jboss.org/forge/view\""));

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
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerFirstName\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerLastName\" value=\"Last Name:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:inputText id=\"customerBeanCustomerLastName\" value=\"#{customerBean.customer.lastName}\"/>\r\n");
      metawidget.append("\t\t\t\t\t<h:message for=\"customerBeanCustomerLastName\" styleClass=\"error\"/>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t\t<h:outputLabel for=\"customerBeanCustomerGroceries\" value=\"Groceries:\"/>\r\n");
      metawidget.append("\t\t\t\t<h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t\t<ui:param name=\"_collection\" value=\"#{customerBean.customer.groceries}\"/>\r\n");
      metawidget
               .append("\t\t\t\t\t<h:dataTable columnClasses=\",remove-column\" id=\"customerBeanCustomerGroceries\" styleClass=\"data-table\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">\r\n");
      metawidget.append("\t\t\t\t\t\t<h:column>\r\n");
      metawidget.append("\t\t\t\t\t\t\t<f:facet name=\"header\">\r\n");
      metawidget.append("\t\t\t\t\t\t\t\t<h:outputText value=\"Name\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t\t</f:facet>\r\n");
      metawidget.append("\t\t\t\t\t\t\t<h:link outcome=\"/scaffold/grocery/view\" value=\"#{_item.name}\">\r\n");
      metawidget.append("\t\t\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t\t</h:link>\r\n");
      metawidget.append("\t\t\t\t\t\t</h:column>\r\n");
      metawidget.append("\t\t\t\t\t\t<h:column>\r\n");
      metawidget
               .append("\t\t\t\t\t\t\t<h:commandLink action=\"#{_collection.remove(_item)}\" styleClass=\"button\" value=\"Remove\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t</h:column>\r\n");
      metawidget.append("\t\t\t\t\t</h:dataTable>\r\n");
      metawidget.append("\t\t\t\t\t<h:panelGroup styleClass=\"buttons\">\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:selectOneMenu converter=\"#{groceryBean.converter}\" id=\"customerBeanCustomerGroceriesAdd\" value=\"#{requestScope['customerBeanCustomerGroceriesAdd']}\">\r\n");
      metawidget.append("\t\t\t\t\t\t\t<f:selectItem/>\r\n");
      metawidget.append("\t\t\t\t\t\t\t<f:selectItems value=\"#{groceryBean.all}\"/>\r\n");
      metawidget.append("\t\t\t\t\t\t</h:selectOneMenu>\r\n");
      metawidget
               .append("\t\t\t\t\t\t<h:commandLink action=\"#{_collection.add(requestScope['customerBeanCustomerGroceriesAdd'])}\" onclick=\"if (document.getElementById(document.forms[0].id+':customerBeanCustomerGroceriesAdd').selectedIndex &lt; 1) { alert('Must select a Grocery'); return false; }\" value=\"Add\"/>\r\n");
      metawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t</h:panelGroup>\r\n");
      metawidget.append("\t\t\t\t<h:outputText/>\r\n");
      metawidget.append("\t\t\t</h:panelGrid>");

      Assert.assertTrue(contents.contains(metawidget));
      Assert.assertTrue(contents.contains("xmlns:forgeview=\"http://jboss.org/forge/view\""));

      // Search

      FileResource<?> search = web.getWebResource("scaffold/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      StringBuilder searchMetawidget = new StringBuilder("<h:form id=\"search\">\r\n");
      searchMetawidget.append("\t\t\t<h:panelGroup styleClass=\"search\">\r\n");
      searchMetawidget.append("\t\t\t\t<h:messages globalOnly=\"true\"/>\r\n\r\n");
      searchMetawidget.append("\t\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchFirstName\" value=\"First Name:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchFirstName\" value=\"#{customerBean.search.firstName}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchFirstName\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchLastName\" value=\"Last Name:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchLastName\" value=\"#{customerBean.search.lastName}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchLastName\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t</h:panelGrid>\r\n");

      Assert.assertTrue(contents.contains(searchMetawidget));

      StringBuilder beanMetawidget = new StringBuilder("\n\t\t\t<h:dataTable id=\"customerBeanPageItems\" styleClass=\"data-table\" value=\"#{customerBean.pageItems}\" var=\"_item\">\r\n");
      beanMetawidget.append("\t\t\t\t<h:column>\r\n");
      beanMetawidget.append("\t\t\t\t\t<f:facet name=\"header\">\r\n");
      beanMetawidget.append("\t\t\t\t\t\t<h:outputText value=\"First Name\"/>\r\n");
      beanMetawidget.append("\t\t\t\t\t</f:facet>\r\n");
      beanMetawidget.append("\t\t\t\t\t<h:link outcome=\"/scaffold/customer/view\" value=\"#{_item.firstName}\">\r\n");
      beanMetawidget.append("\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      beanMetawidget.append("\t\t\t\t\t</h:link>\r\n");
      beanMetawidget.append("\t\t\t\t</h:column>\r\n");
      beanMetawidget.append("\t\t\t\t<h:column>\r\n");
      beanMetawidget.append("\t\t\t\t\t<f:facet name=\"header\">\r\n");
      beanMetawidget.append("\t\t\t\t\t\t<h:outputText value=\"Last Name\"/>\r\n");
      beanMetawidget.append("\t\t\t\t\t</f:facet>\r\n");
      beanMetawidget.append("\t\t\t\t\t<h:link outcome=\"/scaffold/customer/view\" value=\"#{_item.lastName}\">\r\n");
      beanMetawidget.append("\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n");
      beanMetawidget.append("\t\t\t\t\t</h:link>\r\n");
      beanMetawidget.append("\t\t\t\t</h:column>\r\n");
      beanMetawidget.append("\t\t\t</h:dataTable>");

      Assert.assertTrue(contents.contains(beanMetawidget));

      FileResource<?> navigation = web.getWebResource("resources/scaffold/page.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());

      StringBuilder navigationText = new StringBuilder("\n\t\t\t\t<ul>\r\n");
      navigationText.append("\t\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/customer/search\" value=\"Customer\"/>\r\n");
      navigationText.append("\t\t\t\t\t</li>\r\n");
      navigationText.append("\t\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t\t<h:link outcome=\"/scaffold/grocery/search\" value=\"Grocery\"/>\r\n");
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
      FileResource<?> search = web.getWebResource("scaffold/customer/search.xhtml");

      for (FileResource<?> file : Arrays.asList(view, create, search))
      {
         Assert.assertTrue(file.exists());
         String contents = Streams.toString(file.getResourceInputStream());
         Assert.assertTrue(contents.contains(
                  "template=\"/resources/scaffold/page.xhtml"));
      }
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
      getShell().execute("field oneToMany --named field3 --fieldType com.test.domain.Item");
      getShell().execute("field string --named field4");
      getShell().execute("field string --named field5");
      getShell().execute("field string --named field6");
      getShell().execute("field string --named field7");
      getShell().execute("field string --named field8");
      queueInputLines("", "");
      getShell().execute("scaffold from-entity com.test.domain.Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // Search

      FileResource<?> search = web.getWebResource("scaffold/customer/search.xhtml");
      Assert.assertTrue(search.exists());
      String contents = Streams.toString(search.getResourceInputStream());
      Assert.assertTrue(contents.contains(
               "template=\"/resources/scaffold/page.xhtml"));

      StringBuilder searchMetawidget = new StringBuilder("<h:form id=\"search\">\r\n");
      searchMetawidget.append("\t\t\t<h:panelGroup styleClass=\"search\">\r\n" );
      searchMetawidget.append("\t\t\t\t<h:messages globalOnly=\"true\"/>\r\n\r\n");
      searchMetawidget.append("\t\t\t\t<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\">\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchField1\" value=\"Field 1:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchField1\" value=\"#{customerBean.search.field1}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchField1\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchField2\" value=\"Field 2:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchField2\" value=\"#{customerBean.search.field2}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchField2\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchField4\" value=\"Field 4:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchField4\" value=\"#{customerBean.search.field4}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchField4\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchField5\" value=\"Field 5:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchField5\" value=\"#{customerBean.search.field5}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchField5\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputLabel for=\"customerBeanSearchField6\" value=\"Field 6:\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:panelGroup>\r\n");
      searchMetawidget
               .append("\t\t\t\t\t\t<h:inputText id=\"customerBeanSearchField6\" value=\"#{customerBean.search.field6}\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t\t<h:message for=\"customerBeanSearchField6\" styleClass=\"error\"/>\r\n");
      searchMetawidget.append("\t\t\t\t\t</h:panelGroup>\r\n");
      searchMetawidget.append("\t\t\t\t\t<h:outputText/>\r\n");
      searchMetawidget.append("\t\t\t\t</h:panelGrid>\r\n");

      Assert.assertTrue(contents.contains(searchMetawidget));
   }

   private Project setupScaffoldProject() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("HIBERNATE", "JBOSS_AS7", "");
      getShell().execute("persistence setup");
      queueInputLines("", "", "2", "", "", "");
      getShell().execute("scaffold setup");
      return project;
   }
}
