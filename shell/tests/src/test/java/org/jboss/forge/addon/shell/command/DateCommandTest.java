package org.jboss.forge.addon.shell.command;

import static org.hamcrest.CoreMatchers.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

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

/**
 *
 * @author <a href="mailto:md.benhassine@gmail.com">Mahmoud Ben Hassine</a>
 */

@RunWith(Arquillian.class)
public class DateCommandTest
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
   public void testDateCommandWithDefaultPattern() throws Exception
   {
      Result result = shellTest.execute("date", 5, TimeUnit.SECONDS);
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
   }

   @Test
   public void testDateCommandWithLegalPattern() throws Exception
   {
      String formattedDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
      Result result = shellTest.execute("date --pattern yyyyMMdd", 5, TimeUnit.SECONDS);
      Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      String out = shellTest.getStdOut();
      Assert.assertThat(out, containsString(formattedDate));
   }

   @Test
   public void testDateCommandWithIllegalPattern() throws Exception
   {
      Result result = shellTest.execute("date --pattern foo", 5, TimeUnit.SECONDS);
      Assert.assertTrue(result instanceof Failed);
      String out = shellTest.getStdErr();
      Assert.assertThat(out, containsString("Illegal date pattern: foo"));
   }

}
