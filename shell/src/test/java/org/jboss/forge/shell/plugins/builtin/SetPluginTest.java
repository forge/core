/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins.builtin;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class SetPluginTest extends AbstractShellTest
{

   @Test
   public void testSetProperty() throws Exception
   {
      getShell().execute("set");
      Assert.assertFalse(getOutput().contains("ABC=avalue"));
      getShell().execute("set ABC avalue");
      getShell().execute("set");
      Assert.assertTrue(getOutput().contains("ABC=avalue"));
   }

   @Test
   public void testRemoveProperty() throws Exception
   {
      getShell().execute("set ABC avalue");
      getShell().execute("set");
      Assert.assertTrue(getOutput().contains("ABC=avalue"));
      resetOutput();
      getShell().execute("set ABC");
      getShell().execute("set");
      Assert.assertFalse(getOutput().contains("ABC=avalue"));
   }
}
