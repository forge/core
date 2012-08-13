/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.events.PostStartup;
import org.jboss.forge.shell.events.PreShutdown;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Ignore
@RunWith(Arquillian.class)
public class ShellIgnoreEOFTest extends AbstractShellTest
{
   private static boolean shutdown = false;

   public void observeShutdown(@Observes final PreShutdown event)
   {
      ShellIgnoreEOFTest.shutdown = true;
   }

   public void observeShutdown(@Observes final PostStartup event)
   {
      ShellIgnoreEOFTest.shutdown = false;
   }

   @Inject
   @Alias("test-map")
   private MockAbortingPlugin plugin;

   @Test
   public void testIgnoreEOFDefaultsToOne() throws Exception
   {
      assertFalse(shutdown);
      queueInputLines(null, null, null, null, null);
      String line = getShell().readLine();
      assertNull(line);
      line = getShell().readLine();
      assertNull(line);
      assertTrue(shutdown);
   }

   @Test
   public void testSetIgnoreEOF() throws Exception
   {
      assertFalse(shutdown);
      getShell().execute("set IGNOREEOF 7");
      queueInputLines("", null, null, null, null, null);
      getShell().readLine();
      assertNull(getShell().readLine());
      assertNull(getShell().readLine());
      assertNull(getShell().readLine());
      assertNull(getShell().readLine());
      assertNull(getShell().readLine());
      assertFalse(shutdown);
   }

   @Test
   public void testSetIgnoreEOFBadValueDefaultsToOne() throws Exception
   {
      assertFalse(shutdown);
      getShell().execute("set IGNOREEOF foo");
      queueInputLines("", null, null, null, null, null);

      assertEquals("", getShell().readLine());
      assertNull(getShell().readLine());
      assertNull(getShell().readLine());

      assertTrue(shutdown);
   }

   @Test
   public void testEOFAbortsPluginPromptButDoesNotShutdown() throws Exception
   {
      assertFalse(shutdown);
      queueInputLines(null, null);
      assertFalse(plugin.isAborted());
      assertFalse(plugin.isExecuted());
      getShell().execute("test-map");
      assertFalse(shutdown);
      assertTrue(plugin.isExecuted());
      assertTrue(plugin.isAborted());
   }
}
