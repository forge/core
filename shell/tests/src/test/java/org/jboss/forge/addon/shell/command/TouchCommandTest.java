/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class TouchCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:resources"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private ResourceFactory resourceFactory;

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void testTouchNonExistingFile() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);
      Resource<?> child = temp.getChild("foo.txt");
      Assert.assertFalse(child.exists());

      Result result = shellTest.execute("touch foo.txt", 15, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);

      Assert.assertTrue(child.exists());
      child.delete();
   }

   @Test
   public void testTouchExistingFile() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);
      FileResource<?> child = (FileResource<?>) temp.getChild("foo.txt");
      Assert.assertFalse(child.exists());
      child.createNewFile();
      Assert.assertTrue(child.exists());
      long lastModified = child.getLastModified();
      Thread.sleep(1000);

      Result result = shellTest.execute("touch foo.txt", 15, TimeUnit.SECONDS);
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      Assert.assertThat(result.getMessage(), is(not(equalTo(""))));

      long newLastModified = child.getLastModified();
      Assert.assertNotEquals("Last modified not changed for " + child.getFullyQualifiedName(), lastModified,
               newLastModified);

      Assert.assertTrue(child.exists());
      child.delete();
   }
}
