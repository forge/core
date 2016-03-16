/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.archetype.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CreateProjectFromArchetypeTest
{
   ShellTest shellTest;

   @Before
   public void setUp()
   {
      this.shellTest = SimpleContainer.getServices(getClass().getClassLoader(), ShellTest.class).get();
   }

   @Test
   public void testCreateProjectFromArchetype() throws Exception
   {
      String cmd = "project-new --named demo" + System.nanoTime()
               + " --type from-archetype --archetype-group-id org.apache.camel.archetypes --archetype-artifact-id camel-archetype-java --archetype-version 2.16.2";
      Result result = shellTest.execute(cmd, 15, TimeUnit.SECONDS);
      Resource<?> projectRoot = shellTest.getShell().getCurrentResource();
      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertThat(projectRoot.getChild("ReadMe.txt").exists(), is(true));
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

}
