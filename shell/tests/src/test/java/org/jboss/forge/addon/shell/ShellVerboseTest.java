/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
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
public class ShellVerboseTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addClass(ExceptionCommand.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void testVerboseOutput() throws Exception
   {
      shellTest.execute("export VERBOSE=false");
      Thread.sleep(500);
      Result result = shellTest.execute("throw-exception", 15, TimeUnit.SECONDS);
      Assert.assertThat(result, instanceOf(Failed.class));
      Assert.assertThat(shellTest.getStdErr(), not(containsString("Cause Exception")));
      shellTest.clearScreen();
      shellTest.execute("export VERBOSE=true");
      Thread.sleep(500);
      shellTest.execute("throw-exception", 15, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdErr(), containsString("Cause Exception"));
   }

   private static class ExceptionCommand extends AbstractUICommand
   {
      @Override
      public Result execute(UIExecutionContext context) throws Exception
      {
         throw new RuntimeException("Expected Exception", new Exception("Cause Exception"));
      }

      @Override
      public UICommandMetadata getMetadata(UIContext context)
      {
         return Metadata.forCommand(getClass()).name("throw-exception");
      }

      @Override
      public void initializeUI(UIBuilder builder) throws Exception
      {
      }

   }

}
