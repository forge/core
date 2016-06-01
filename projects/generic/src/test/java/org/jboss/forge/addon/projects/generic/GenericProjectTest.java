/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.generic;
/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import static org.hamcrest.CoreMatchers.instanceOf;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.generic.facets.GenericMetadataFacet;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class GenericProjectTest
{
   private ProjectFactory projectFactory;
   private GenericProjectProvider projectProvider;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      projectProvider = SimpleContainer.getServices(getClass().getClassLoader(), GenericProjectProvider.class).get();
   }

   @Test
   public void testCreateNewGenericProject()
   {
      Project project = projectFactory.createTempProject(projectProvider);
      Assert.assertThat(project, instanceOf(GenericProject.class));
      MetadataFacet facet = project.getFacet(MetadataFacet.class);
      Assert.assertThat(facet, instanceOf(GenericMetadataFacet.class));
      facet.setProjectName("myproject");
      Assert.assertEquals("myproject", facet.getProjectName());
   }
}
