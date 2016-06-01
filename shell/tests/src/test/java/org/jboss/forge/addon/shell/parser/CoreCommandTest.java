/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.mock.command.Career;
import org.jboss.forge.addon.shell.mock.command.FooCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CoreCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClasses(FooCommand.class, Career.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private ShellTest test;

   @Inject
   private ResourceFactory resourceFactory;

   @After
   public void tearDown() throws Exception
   {
      test.close();
   }

   @Test
   public void testEscapes() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      tempDir.deleteOnExit();
      DirectoryResource currentResource = resourceFactory.create(DirectoryResource.class, tempDir);
      Shell shell = test.getShell();
      shell.setCurrentResource(currentResource);
      DirectoryResource child = currentResource.getChildDirectory("Forge 2 Escape");
      child.mkdir();
      child.deleteOnExit();
      Result result = test.execute("cd Forge\\ 2\\ Escape", 10, TimeUnit.SECONDS);
      Assert.assertThat(result.getMessage(), CoreMatchers.nullValue());
      Assert.assertEquals(shell.getCurrentResource(), child);
      currentResource.delete(true);
   }

   @Test
   public void testQuotes() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      tempDir.deleteOnExit();
      DirectoryResource currentResource = resourceFactory.create(DirectoryResource.class, tempDir);
      Shell shell = test.getShell();
      shell.setCurrentResource(currentResource);
      FileResource<?> child = currentResource.getChildDirectory("Forge 2 Escape");
      child.mkdir();
      child.deleteOnExit();
      Result result = test.execute("cd \"Forge 2 Escape\"", 10, TimeUnit.SECONDS);
      Assert.assertThat(result.getMessage(), nullValue());
      Assert.assertEquals(shell.getCurrentResource(), child);
      currentResource.delete(true);
   }
}
