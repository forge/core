/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.scenario.weather;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
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
public class FacesScaffoldWeatherTest extends AbstractFacesScaffoldTest
{
   @Test
   public void testGenerate() throws Exception
   {
      Project project = setupScaffoldProject();

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

      FileResource<?> view = web.getWebResource("/continent/view.xhtml");
      Assert.assertTrue(view.exists());
      String contents = Streams.toString(view.getResourceInputStream());

      Assert.assertTrue(contents
               .contains("<h:dataTable id=\"continentBeanContinentHurricanes\" styleClass=\"data-table\" value=\"#{forgeview:asList(continentBean.continent.hurricanes)}\" var=\"_item\">"));

      getShell().execute("build");
   }
}
