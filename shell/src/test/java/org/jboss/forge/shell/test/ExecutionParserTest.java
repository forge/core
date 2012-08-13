/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.test.command.MockOptionTestPlugin;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ExecutionParserTest extends AbstractShellTest
{
   @Inject
   @Alias("motp")
   private MockOptionTestPlugin plugin;

   @Test
   public void testInvalidSuppliedOptionIsCorrected() throws Exception
   {
      String packg = "com.example.good.pkg";
      queueInputLines(packg);
      getShell().execute("motp suppliedOption --package bad_%package");
      assertEquals(packg, plugin.getSuppliedOption());
   }

   @Test
   public void testOmittedRequiredOptionIsFilled() throws Exception
   {
      String packg = "com.example.good.pkg";
      queueInputLines("another#$%bad($package", packg);
      getShell().execute("motp requiredOption");
      assertEquals(packg, plugin.getRequiredOption());
   }

   @Test
   public void testDefaultCommandPassedExecutedLiterallyTreatedAsArg() throws Exception
   {
      getShell().execute("motp motp");
      assertEquals("motp", plugin.getDefaultCommandArg());
   }

   @Test
   public void testOmittedOptionalBooleanDefaultsToFalse() throws Exception
   {
      assertNull(plugin.getBooleanOptionOmitted());
      getShell().execute("motp booleanOptionOmitted");
      assertEquals(false, plugin.getBooleanOptionOmitted());
   }

   @Test
   public void testVarargsTakesUnusedParameters() throws Exception
   {
      getShell().execute("motp varargsOption -Pfoo --bar -ext");
      assertEquals(3, plugin.getVarargsOptions().size());
      assertTrue(plugin.getVarargsOptions().contains("-Pfoo"));
      assertTrue(plugin.getVarargsOptions().contains("--bar"));
      assertTrue(plugin.getVarargsOptions().contains("-ext"));
   }
}
