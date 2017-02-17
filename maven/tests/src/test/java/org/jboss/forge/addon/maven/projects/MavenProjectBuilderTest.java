/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.building.ProjectBuilder;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class MavenProjectBuilderTest
{

   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

   /**
    * Test method for {@link org.jboss.forge.addon.maven.projects.MavenProjectBuilder#build()}.
    */
   @Test
   public void testBuild() throws Exception
   {
      Project project = projectFactory.createTempProject();
      ProjectBuilder projectBuilder = project.getFacet(PackagingFacet.class).createBuilder();
      projectBuilder.profiles("foo", "bar");
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();
      projectBuilder.build(new PrintStream(out, true), new PrintStream(err, true));
      assertThat(out.toString()).contains("BUILD SUCCESS",
               "[WARNING] The requested profile \"foo\" could not be activated because it does not exist.",
               "[WARNING] The requested profile \"bar\" could not be activated because it does not exist.");
      assertThat(err.toString()).isEmpty();

   }

}
