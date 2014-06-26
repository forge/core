/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.command;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.spi.command.CdTokenHandler;
import org.jboss.forge.addon.shell.spi.command.CdTokenHandlerFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CdCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private CdTokenHandlerFactory handlerFactory;

   @Test
   public void testCDProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      String projectPath = project.getRoot().getFullyQualifiedName();
      shellTest.execute("cd " + projectPath, 5, TimeUnit.SECONDS);
      shellTest.clearScreen();
      shellTest.execute("pwd", 5, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString(projectPath));

      shellTest.execute("mkdir abc", 5, TimeUnit.SECONDS);
      shellTest.execute("cd abc", 5, TimeUnit.SECONDS);
      shellTest.execute("cd ~~", 5, TimeUnit.SECONDS);
      shellTest.clearScreen();
      shellTest.execute("pwd", 5, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString(projectPath));
   }

   @Test
   public void testManualHandlerAddition() throws Exception
   {
      Project project = projectFactory.createTempProject();
      String projectPath = project.getRoot().getFullyQualifiedName();

      CdTokenHandler handler = new CdTokenHandler()
      {
         @Override
         public List<Resource<?>> getNewCurrentResources(UIContext current, String token)
         {
            return Projects.getSelectedProject(projectFactory, current).getRoot().getChild("src").listResources();
         }
      };

      Assert.assertFalse(handlerFactory.getHandlers().contains(handler));
      ListenerRegistration<CdTokenHandler> registration = handlerFactory.addTokenHandler(handler);
      try
      {
         Assert.assertTrue(handlerFactory.getHandlers().contains(handler));

         shellTest.execute("cd " + projectPath, 5, TimeUnit.SECONDS);
         shellTest.clearScreen();
         shellTest.execute("pwd", 5, TimeUnit.SECONDS);
         Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString(projectPath));

         shellTest.execute("mkdir abc", 5, TimeUnit.SECONDS);
         shellTest.execute("cd abc", 5, TimeUnit.SECONDS);
         shellTest.execute("cd #/", 5, TimeUnit.SECONDS);
         shellTest.clearScreen();
         shellTest.execute("pwd", 5, TimeUnit.SECONDS);
         Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString(projectPath));
      }
      finally
      {
         registration.removeListener();
      }
   }
}
