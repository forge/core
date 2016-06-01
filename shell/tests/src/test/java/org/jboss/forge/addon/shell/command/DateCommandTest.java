/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:md.benhassine@gmail.com">Mahmoud Ben Hassine</a>
 */

@RunWith(Arquillian.class)
public class DateCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness"),
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
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Before
   public void setUp() throws Exception
   {
      shellTest.clearScreen();
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void testDateCommandWithDefaultPattern() throws Exception
   {
      Result result = shellTest.execute("date", 15, TimeUnit.SECONDS);
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
   }

   @Test
   public void testDateCommandWithLegalPattern() throws Exception
   {
      String formattedDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
      Result result = shellTest.execute("date --pattern yyyyMMdd", 15, TimeUnit.SECONDS);
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      String out = shellTest.getStdOut();
      Assert.assertThat(out, containsString(formattedDate));
   }

   @Test
   public void testDateCommandWithIllegalPattern() throws Exception
   {
      Result result = shellTest.execute("date --pattern foo", 15, TimeUnit.SECONDS);
      Assert.assertTrue(result instanceof Failed);
      String out = shellTest.getStdErr();
      Assert.assertThat(out, containsString("Illegal date pattern: foo"));
   }

}
