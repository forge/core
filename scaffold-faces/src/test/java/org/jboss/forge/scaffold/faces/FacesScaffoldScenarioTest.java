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
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Richard Kennard
 */

@RunWith(Arquillian.class)
public class FacesScaffoldScenarioTest extends AbstractShellTest
{
   /**
    * Lincoln's example domain model from 2nd Dec 2011.
    */

   @Test
   public void testGenerateOneToManyEntity() throws Exception
   {
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Customer");
      getShell().execute("entity --named Address");
      getShell().execute("field string --named street");
      getShell().execute("field string --named city");
      getShell().execute("field string --named state");
      getShell().execute("field string --named zipCode");
      getShell().execute("field manyToMany --named customers --fieldType com.test.domain.Customer");
      getShell().execute("entity --named Item");
      getShell().execute("field string --named name");
      getShell().execute("field string --named description");
      getShell().execute("entity --named Profile");
      getShell().execute("field string --named bio");
      //TODO:getShell().execute("field oneToOne --named customer --fieldType com.test.domain.Customer --inverseFieldName profile");
      getShell().execute("entity --named SubmittedOrder");
      getShell().execute("field manyToOne --named customer --fieldType com.test.domain.Customer");
      getShell().execute("field manyToOne --named address --fieldType com.test.domain.Address");
      getShell().execute("entity --named Customer");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field manyToMany --named addresses --fieldType com.test.domain.Address");
      getShell().execute("field oneToMany --named orders --fieldType com.test.domain.SubmittedOrder");
      getShell().execute("field oneToOne --named profile --fieldType com.test.domain.Profile");

      // (need to specify all entities until https://issues.jboss.org/browse/FORGE-392)

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.domain.Address com.test.domain.Item com.test.domain.Profile com.test.domain.SubmittedOrder com.test.domain.Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // View

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      Assert.assertTrue(view.exists());
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
