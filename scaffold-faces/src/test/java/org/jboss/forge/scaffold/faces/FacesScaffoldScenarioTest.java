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

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.util.Streams;
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
      getShell().execute("field manyToMany --named customers --fieldType com.test.domain.Customer");
      getShell().execute("entity --named Item");
      getShell().execute("field string --named name");
      getShell().execute("field number --named price --type java.lang.Double");
      getShell().execute("field string --named description");
      getShell().execute("entity --named Profile");
      getShell().execute("field string --named bio");
      getShell().execute("field string --named URL");
      // Needs https://issues.jboss.org/browse/FORGE-397:
      // getShell().execute("field oneToOne --named customer --fieldType com.test.domain.Customer --inverseFieldName profile");
      getShell().execute("entity --named SubmittedOrder");
      getShell().execute("field manyToOne --named Customer --fieldType com.test.domain.Customer");
      getShell().execute("field manyToOne --named address --fieldType com.test.domain.Address");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field temporal --type DATE --named birthdate");
      getShell().execute("field manyToMany --named addresses --fieldType com.test.domain.Address");
      getShell().execute("field oneToMany --named orders --fieldType com.test.domain.SubmittedOrder");
      getShell().execute("field oneToOne --named profile --fieldType com.test.domain.Profile");

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.domain.*");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // Check create screen has 'Create New Profile'

      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
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

      create = web.getWebResource("scaffold/address/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());

      Assert.assertTrue(contents.contains("\"#{addressBean.address.city}\""));
      Assert.assertTrue(!contents.contains(".City"));

      // View

      FileResource<?> view = web.getWebResource("scaffold/address/view.xhtml");
      Assert.assertTrue(view.exists());
      contents = Streams.toString(view.getResourceInputStream());

      Assert.assertTrue(contents.contains("\"#{addressBean.address.city}\""));
      Assert.assertTrue(!contents.contains(".City"));

      // Search

      FileResource<?> search = web.getWebResource("scaffold/address/search.xhtml");
      Assert.assertTrue(search.exists());
      contents = Streams.toString(search.getResourceInputStream());

      Assert.assertTrue(contents.contains("\"#{addressBean.search.city}\""));
      Assert.assertTrue(contents.contains("\"#{_item.city}\""));
      Assert.assertTrue(!contents.contains(".City"));

      // Backing Bean

      FileResource<?> customerBean = java.getJavaResource("/com/test/view/AddressBean.java");
      Assert.assertTrue(customerBean.exists());
      contents = Streams.toString(customerBean.getResourceInputStream());

      StringBuilder qbeMetawidget = new StringBuilder(
               "\t\tString City = this.search.getCity();\r\n");
      qbeMetawidget.append("\t\tif (City != null && !\"\".equals(City)) {\r\n");
      qbeMetawidget
               .append("\t\t\tpredicatesList.add(builder.like(root.<String>get(\"City\"), '%' + City + '%'));\r\n");
      qbeMetawidget.append("\t\t}\r\n");

      Assert.assertTrue(contents.contains(qbeMetawidget));

      getShell().execute("build");
   }

   @Test
   public void testGenerateScenario2() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Hurricane");
      getShell().execute("field string --named name");
      getShell().execute("entity --named Continent");
      getShell().execute("field string --named name");
      getShell()
               .execute("field manyToMany --named hurricanes --fieldType com.test.domain.Hurricane --inverseFieldName continents");

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.domain.*");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("scaffold/continent/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());

      Assert.assertTrue(contents
               .contains("<h:dataTable id=\"continentBeanContinentHurricanes\" styleClass=\"data-table\" value=\"#{forgeview:asList(continentBean.continent.hurricanes)}\" var=\"_item\">"));

      getShell().execute("build");
   }
}
