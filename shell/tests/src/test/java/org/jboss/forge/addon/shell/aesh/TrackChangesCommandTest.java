/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.File;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;
import org.jboss.forge.addon.shell.mock.MockCommandExecutionListener;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
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
public class TrackChangesCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addClass(MockCommandExecutionListener.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   private static final int SHELL_TIMEOUT = 5;

   @Inject
   private ResourceFactory factory;

   @Inject
   private ShellTest test;

   @After
   public void tearDown() throws Exception
   {
      test.close();
   }

   @Test(timeout = 10000)
   public void testTransactionTrackChanges() throws Exception
   {
      test.clearScreen();
      DirectoryResource tempDir = factory.create(OperatingSystemUtils.createTempDir()).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      test.getShell().setCurrentResource(tempDir);
      Assert.assertFalse(test.execute("track-changes", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Resource change tracking is ON.", SHELL_TIMEOUT, TimeUnit.SECONDS);

      Assert.assertFalse(test.execute("touch foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Created  " + tempDir.getFullyQualifiedName() + File.separator + "foo.txt",
               SHELL_TIMEOUT, TimeUnit.SECONDS);

      Assert.assertFalse(test.execute("rm foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Deleted  " + tempDir.getFullyQualifiedName() + File.separator +
               "foo.txt", SHELL_TIMEOUT,
               TimeUnit.SECONDS);

      Assert.assertFalse(test.execute("track-changes", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Resource change tracking is OFF.", SHELL_TIMEOUT, TimeUnit.SECONDS);
   }

   @Test(timeout = 10000)
   public void testTransactionTrackChangesExistingTransaction() throws Exception
   {
      test.clearScreen();
      DirectoryResource tempDir = factory.create(OperatingSystemUtils.createTempDir()).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      test.getShell().setCurrentResource(tempDir);
      Assert.assertFalse(test.execute("transaction-start", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertFalse(test.execute("track-changes", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);

      Assert.assertFalse(test.execute("touch foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertFalse(test.execute("rm foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);

      test.clearScreen();
      Assert.assertFalse(test.execute("transaction-commit", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Created  " + tempDir.getFullyQualifiedName() + File.separator
               + "foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS);
      test.waitForStdOutValue("Deleted  " + tempDir.getFullyQualifiedName() + File.separator
               + "foo.txt", SHELL_TIMEOUT,
               TimeUnit.SECONDS);

      Assert.assertFalse(test.execute("track-changes", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Resource change tracking is OFF.", SHELL_TIMEOUT, TimeUnit.SECONDS);
   }

   @Test(timeout = 10000)
   public void testTransactionTrackChangesManualTransaction() throws Exception
   {
      test.clearScreen();
      DirectoryResource tempDir = factory.create(OperatingSystemUtils.createTempDir()).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      test.getShell().setCurrentResource(tempDir);
      Assert.assertFalse(test.execute("track-changes", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertFalse(test.execute("transaction-start", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);

      Assert.assertFalse(test.execute("touch foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertFalse(test.execute("rm foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);

      test.clearScreen();
      Assert.assertFalse(test.execute("transaction-commit", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Created  " + tempDir.getFullyQualifiedName() + File.separator + "foo.txt",
               SHELL_TIMEOUT, TimeUnit.SECONDS);
      test.waitForStdOutValue("Deleted  " + tempDir.getFullyQualifiedName() + File.separator + "foo.txt",
               SHELL_TIMEOUT,
               TimeUnit.SECONDS);

      test.clearScreen();
      Assert.assertFalse(test.execute("track-changes", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Resource change tracking is OFF.", SHELL_TIMEOUT, TimeUnit.SECONDS);
      Assert.assertFalse(test.getStdOut().contains(
               "Modified " + tempDir.getFullyQualifiedName() + File.separator + "foo.txt"));
      Assert.assertFalse(test.getStdOut().contains(
               "Deleted  " + tempDir.getFullyQualifiedName() + File.separator + "foo.txt"));
   }

   @Test(timeout = 10000)
   public void testTransactionTrackChangesOff() throws Exception
   {
      DirectoryResource tempDir = factory.create(OperatingSystemUtils.createTempDir()).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      final AtomicBoolean flag = new AtomicBoolean(false);
      factory.addTransactionListener(new ResourceTransactionListener()
      {
         @Override
         public void transactionStarted(ResourceTransaction transaction)
         {
            flag.set(true);
         }

         @Override
         public void transactionRolledBack(ResourceTransaction transaction)
         {
            flag.set(true);
         }

         @Override
         public void transactionCommitted(ResourceTransaction transaction, Set<ResourceEvent> changeSet)
         {
            flag.set(true);
         }
      });

      test.clearScreen();
      test.getShell().setCurrentResource(tempDir);

      Assert.assertFalse(test.execute("touch foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);

      Assert.assertFalse(flag.get());

      Assert.assertFalse(test.execute("track-changes", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Resource change tracking is ON.", SHELL_TIMEOUT, TimeUnit.SECONDS);

      Assert.assertFalse(test.execute("rm foo.txt", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Deleted  " + tempDir.getFullyQualifiedName() + File.separator + "foo.txt",
               SHELL_TIMEOUT,
               TimeUnit.SECONDS);

      Assert.assertTrue(flag.get());

      Assert.assertFalse(test.execute("track-changes", SHELL_TIMEOUT, TimeUnit.SECONDS) instanceof Failed);
      test.waitForStdOutValue("Resource change tracking is OFF.", SHELL_TIMEOUT, TimeUnit.SECONDS);
   }
}
