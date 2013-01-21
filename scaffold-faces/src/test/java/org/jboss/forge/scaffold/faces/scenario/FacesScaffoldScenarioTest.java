/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.scenario;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.scaffold.faces.AbstractFacesScaffoldTest;
import org.jboss.forge.shell.util.Streams;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Richard Kennard
 */

@RunWith(Arquillian.class)
public class FacesScaffoldScenarioTest extends AbstractFacesScaffoldTest
{
   /**
    * Lincoln's example domain model from 2nd Dec 2011.
    */

   @Test
   public void testGenerateScenario1() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Customer");
      getShell().execute("entity --named Address");
      getShell().execute("field string --named street");
      // Testing FORGE-402 (Uppercase fields)
      getShell().execute("field string --named City");
      getShell().execute("field string --named state");
      getShell().execute("field int --named zipCode");
      getShell().execute("field manyToMany --named customers --fieldType com.test.model.Customer");
      getShell().execute("entity --named Item");
      getShell().execute("field string --named name");
      getShell().execute("field number --named price --type java.lang.Double");
      getShell().execute("field string --named description");
      getShell().execute("entity --named Profile");
      getShell().execute("field string --named bio");
      getShell().execute("field string --named URL");
      // Needs https://issues.jboss.org/browse/FORGE-397:
      // getShell().execute("field oneToOne --named customer --fieldType com.test.model.Customer --inverseFieldName profile");
      getShell().execute("entity --named SubmittedOrder");
      getShell().execute("field manyToOne --named Customer --fieldType com.test.model.Customer");
      getShell().execute("field manyToOne --named address --fieldType com.test.model.Address");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field temporal --type DATE --named birthdate");
      getShell().execute("field manyToMany --named addresses --fieldType com.test.model.Address");
      getShell().execute("field oneToMany --named orders --fieldType com.test.model.SubmittedOrder");
      getShell().execute("field oneToOne --named profile --fieldType com.test.model.Profile");

      queueInputLines("", "", "", "", "");
      getShell().execute("scaffold from-entity com.test.model.*");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // Check create screen has 'Create New Profile'

      FileResource<?> create = web.getWebResource("/customer/create.xhtml");
      Assert.assertTrue(create.exists());
      String contents = Streams.toString(create.getResourceInputStream());

      Assert.assertTrue(contents
               .contains("<h:commandLink action=\"#{customerBean.customer.newProfile}\" rendered=\"#{empty customerBean.customer.profile}\" value=\"Create New Profile\"/>"));
      Assert.assertTrue(contents
               .contains("<h:panelGrid columnClasses=\"label,component,required\" columns=\"3\" rendered=\"#{!empty customerBean.customer.profile}\">"));
      Assert.assertTrue(contents.contains("<h:outputLabel for=\"customerBeanCustomerProfileBio\" value=\"Bio:\"/>"));
      Assert.assertTrue(contents
               .contains("<h:inputText id=\"customerBeanCustomerProfileBio\" value=\"#{customerBean.customer.profile.bio}\"/>"));

      // Check 'City' is dealt with correctly (lowercase in JSF, uppercase in Java code)

      create = web.getWebResource("/address/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());

      Assert.assertTrue(contents.contains("\"#{addressBean.address.city}\""));
      Assert.assertTrue(!contents.contains(".City"));

      // View

      FileResource<?> view = web.getWebResource("/address/view.xhtml");
      Assert.assertTrue(view.exists());
      contents = Streams.toString(view.getResourceInputStream());

      Assert.assertTrue(contents.contains("\"#{addressBean.address.city}\""));
      Assert.assertTrue(!contents.contains(".City"));

      // Search

      FileResource<?> search = web.getWebResource("/address/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());

      Assert.assertTrue(contents.contains("\"#{addressBean.example.city}\""));
      Assert.assertTrue(contents.contains("\"#{_item.city}\""));
      Assert.assertTrue(!contents.contains(".City"));

      // Backing Bean

      FileResource<?> customerBean = java.getJavaResource("/com/test/view/AddressBean.java");
      Assert.assertTrue(customerBean.exists());
      contents = Streams.toString(customerBean.getResourceInputStream());

      StringBuilder qbeMetawidget = new StringBuilder(
               "      String City = this.example.getCity();\n");
      qbeMetawidget.append("      if (City != null && !\"\".equals(City)) {\n");
      qbeMetawidget
               .append("         predicatesList.add(builder.like(root.<String> get(\"City\"), '%' + City + '%'));\n");
      qbeMetawidget.append("      }\n");

      Assert.assertTrue(normalized(contents).contains(normalized(qbeMetawidget)));

      FileResource<?> welcomeFile = web.getWebResource("/index.html");
      Assert.assertTrue(welcomeFile.exists());
      contents = Streams.toString(welcomeFile.getResourceInputStream());
      Assert.assertTrue(contents.contains("/faces/index.xhtml"));

      getShell().execute("build");
   }

   @Test
   public void testGenerateScenario2() throws Exception
   {
      Project project = setupScaffoldProject("weather");

      queueInputLines("");
      getShell().execute("entity --named Hurricane");
      getShell().execute("field string --named name");
      getShell().execute("entity --named Continent");
      getShell().execute("field string --named name");
      getShell()
               .execute("field manyToMany --named hurricanes --fieldType com.test.model.Hurricane --inverseFieldName continents");

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.model.*");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("weather/continent/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());

      Assert.assertTrue(contents
               .contains("<h:dataTable id=\"continentBeanContinentHurricanes\" styleClass=\"data-table\" value=\"#{forgeview:asList(continentBean.continent.hurricanes)}\" var=\"_item\">"));



      FileResource<?> navigation = web.getWebResource("resources/scaffold/pageTemplate.xhtml");
      Assert.assertTrue(navigation.exists());
      contents = Streams.toString(navigation.getResourceInputStream());
      Assert.assertTrue(contents.contains(" styleClass=\"brand\">Test</h:link>"));
      Assert.assertTrue(!contents.contains("@{appName}"));

      StringBuilder navigationText = new StringBuilder("\n\t\t\t<ul>\r\n");
      navigationText.append("\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t<h:link outcome=\"/weather/continent/search\" value=\"Continent\"/>\r\n");
      navigationText.append("\t\t\t\t</li>\r\n");
      navigationText.append("\t\t\t\t<li>\r\n");
      navigationText
               .append("\t\t\t\t\t<h:link outcome=\"/weather/hurricane/search\" value=\"Hurricane\"/>\r\n");
      navigationText.append("\t\t\t\t</li>\r\n");

      Assert.assertTrue(contents.contains(navigationText));

      FileResource<?> index = web.getWebResource("weather/index.xhtml");
      Assert.assertTrue(index.exists());
      FileResource<?> welcomeFile = web.getWebResource("weather/index.html");
      Assert.assertTrue(welcomeFile.exists());
      contents = Streams.toString(welcomeFile.getResourceInputStream());
      Assert.assertTrue(contents.contains("/faces/weather/index.xhtml"));

      getShell().execute("build");
   }
}
