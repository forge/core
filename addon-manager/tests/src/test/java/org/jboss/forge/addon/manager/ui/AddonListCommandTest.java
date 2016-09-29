/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.ui;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonListCommandTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDependency(name = "org.jboss.forge.addon:addon-manager"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addAsServiceProvider(Service.class, AddonListCommandTest.class);
   }

   private final int timeoutQuantity = 10;

   private ShellTest test;

   @Before
   public void setUp()
   {
      test = SimpleContainer.getServices(getClass().getClassLoader(), ShellTest.class).get();
   }

   @After
   public void tearDown() throws Exception
   {
      test.close();
   }

   @Test
   public void testAddonListCommand() throws Exception
   {
      test.clearScreen();
      Result result = test.execute("addon-list", timeoutQuantity, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);
      String out = test.getStdOut();
      Assert.assertThat(out, containsString("org.jboss.forge.addon:addon-manager"));
   }

}
