/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.TestShellConfiguration;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ShellCommandListenerTest
{
   @Deployment
   @Dependencies({ @Addon(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:shell-test-harness", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(MockCommandExecutionListener.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:shell-test-harness",
                                 "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ForgeShell shell;

   @Inject
   private TestShellConfiguration streams;

   @Test
   public void testCommandExecutionListenerTriggers() throws Exception
   {
      Assert.assertNotNull(shell);

      MockCommandExecutionListener listener = new MockCommandExecutionListener();
      shell.addCommandExecutionListener(listener);

      streams.getStdIn().write("list-services\n".getBytes());

      long start = System.currentTimeMillis();
      while (!listener.isPreExecuted() || !listener.isPostExecuted())
      {
         Thread.sleep(10);
         Assert.assertTrue("Timed out.", System.currentTimeMillis() - start < 5000);
      }

      System.out.println("OUT:" + streams.getStdOut().toString());
      System.out.println("ERR:" + streams.getStdErr().toString());

      Assert.assertTrue(listener.isPreExecuted());
      Assert.assertTrue(listener.isPostExecuted());
   }

}
