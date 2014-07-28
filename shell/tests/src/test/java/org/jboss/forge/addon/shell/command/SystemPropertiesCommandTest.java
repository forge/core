/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.command;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;

/**
 * @author <a href="mailto:md.benhassine@gmail.com">Mahmoud Ben Hassine</a>
 */
@RunWith(Arquillian.class)
public class SystemPropertiesCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Test
   public void testListSystemProperties() throws Exception
   {
      Result result = shellTest.execute("system-property-get", 5, TimeUnit.SECONDS);
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      String out = shellTest.getStdOut();
      //assert that console output contains some predefined system properties
      Assert.assertThat(out, containsString("user.name"));
      Assert.assertThat(out, containsString("user.home"));
      Assert.assertThat(out, containsString("java.version"));
      Assert.assertThat(out, containsString("java.class.path"));
   }

   @Test
   public void testSetSystemProperty() throws Exception
   {
      Result result = shellTest.execute("system-property-set --named foo --value bar", 5, TimeUnit.SECONDS);
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      Assert.assertEquals("bar", System.getProperty("foo"));
   }

   @Test
   public void testGetSystemProperty() throws Exception
   {
      System.setProperty("foo", "bar");
      Result result = shellTest.execute("system-property-get --named foo", 5, TimeUnit.SECONDS);
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      Assert.assertThat(shellTest.getStdOut(), containsString("bar"));
   }

    @Test
    public void testGetUnknownSystemProperty() throws Exception
    {
        Result result = shellTest.execute("system-property-get --named blah", 5, TimeUnit.SECONDS);
        Assert.assertThat(result, is(not(instanceOf(Failed.class))));
        Assert.assertEquals(result.getMessage(), null);
    }

}
