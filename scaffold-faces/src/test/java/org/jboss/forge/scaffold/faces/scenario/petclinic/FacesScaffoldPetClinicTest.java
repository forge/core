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
package org.jboss.forge.scaffold.faces.scenario.petclinic;

import java.util.Arrays;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.scaffold.faces.AbstractFacesScaffoldTest;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.test.web.WebTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Burr's example domain model from 7th Dec 2011.
 *
 * @author Richard Kennard
 */

@RunWith(Arquillian.class)
public class FacesScaffoldPetClinicTest extends AbstractFacesScaffoldTest
{
   @Inject
   private WebTest webTest;

   @Test
   public void testGenerate() throws Exception
   {
      Project current = getShell().getCurrentProject();
      Project project = setupScaffoldProject("petClinic");

      queueInputLines("");
      getShell().execute("entity --named Owner");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field string --named address");
      getShell().execute("field string --named city");
      getShell().execute("field string --named telephone");
      getShell().execute("field string --named homePage");
      getShell().execute("field string --named email");
      getShell().execute("field date --named birthday");
      getShell().execute("entity --named Vet");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field string --named address");
      getShell().execute("field string --named city");
      getShell().execute("field string --named telephone");
      getShell().execute("field string --named homePage");
      getShell().execute("field string --named email");
      getShell().execute("field date --named birthday");
      getShell().execute("field date --named employedSince");
      getShell().execute("field int --named specialty");
      getShell().execute("entity --named Pet");
      getShell().execute("field string --named name");
      getShell().execute("field float --named weight");
      getShell().execute("field int --named type");
      getShell().execute("field boolean --named sendReminders");
      getShell().execute("field manyToOne --named owner --fieldType com.test.model.Owner");
      getShell().execute("entity --named Visit");
      getShell().execute("field string --named description");
      getShell().execute("field data --named visitDate");
      getShell().execute("field manyToOne --named pet --fieldType com.test.model.Pet");
      getShell().execute("field manyToOne --named vet --fieldType com.test.model.Vet");

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.model.*");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // Check search screen has h:message

      FileResource<?> search = web.getWebResource("/petClinic/pet/search.xhtml");
      Assert.assertTrue(search.exists());
      String contents = Streams.toString(search.getResourceInputStream());

      String metawidget = "\t\t\t\t\t<h:outputLabel for=\"petBeanSearchType\" value=\"Type:\"/>\r\n" +
               "\t\t\t\t\t<h:panelGroup>\r\n" +
               "\t\t\t\t\t\t<h:inputText id=\"petBeanSearchType\" value=\"#{petBean.search.type}\"/>\r\n" +
               "\t\t\t\t\t\t<h:message for=\"petBeanSearchType\" styleClass=\"error\"/>\r\n" +
               "\t\t\t\t\t</h:panelGroup>";

      Assert.assertTrue(contents.contains(metawidget));

      // Check search screen has boolean graphic

      metawidget = "\t\t\t\t\t<h:link outcome=\"/petClinic/pet/view\">\r\n";
      metawidget += "\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n";
      metawidget += "\t\t\t\t\t\t<h:outputText styleClass=\"#{_item.sendReminders ? 'boolean-true' : 'boolean-false'}\"/>\r\n";
      metawidget += "\t\t\t\t\t</h:link>\r\n";

      Assert.assertTrue(contents.contains(metawidget));

      metawidget = "\t\t\t\t\t<h:link outcome=\"/petClinic/pet/view\">\r\n";
      metawidget += "\t\t\t\t\t\t<f:param name=\"id\" value=\"#{_item.id}\"/>\r\n";
      metawidget += "\t\t\t\t\t\t<h:outputText id=\"itemOwner\" value=\"#{_item.owner}\"/>\r\n";
      metawidget += "\t\t\t\t\t</h:link>\r\n";

      Assert.assertTrue(contents.contains(metawidget));

      // Check create screen has h:selectBooleanCheckbox

      FileResource<?> create = web.getWebResource("/petClinic/pet/create.xhtml");
      Assert.assertTrue(create.exists());
      contents = Streams.toString(create.getResourceInputStream());

      metawidget = "\t\t\t\t<h:outputLabel for=\"petBeanPetSendReminders\" value=\"Send Reminders:\"/>\r\n";
      metawidget += "\t\t\t\t<h:panelGroup>\r\n";
      metawidget += "\t\t\t\t\t<h:selectBooleanCheckbox id=\"petBeanPetSendReminders\" value=\"#{petBean.pet.sendReminders}\"/>\r\n";
      metawidget += "\t\t\t\t\t<h:message for=\"petBeanPetSendReminders\" styleClass=\"error\"/>\r\n";
      metawidget += "\t\t\t\t</h:panelGroup>";

      Assert.assertTrue(contents.contains(metawidget));

      // Check view screen has boolean graphic

      FileResource<?> view = web.getWebResource("/petClinic/pet/view.xhtml");
      Assert.assertTrue(view.exists());
      contents = Streams.toString(view.getResourceInputStream());

      metawidget = "\t\t\t<h:outputLabel value=\"Send Reminders:\"/>\r\n";
      metawidget += "\t\t\t<h:outputText styleClass=\"#{petBean.pet.sendReminders ? 'boolean-true' : 'boolean-false'}\"/>";

      Assert.assertTrue(contents.contains(metawidget));

      // Deploy to a real container and test

      this.webTest.setup(project);
      JavaClass clazz = this.webTest.from(current, FacesScaffoldPetClinicClient.class);

      this.webTest.buildDefaultDeploymentMethod(project, clazz, Arrays.asList(
               ".addAsResource(\"META-INF/persistence.xml\", \"META-INF/persistence.xml\")"
               ));
      this.webTest.addAsTestClass(project, clazz);

      getShell().execute("build");
   }
}
