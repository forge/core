/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.test.command;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class CommandScopedTest extends AbstractShellTest
{

   @Test
   public void testCommandScoped() throws Exception
   {
      getShell().execute("cmdscope");
      Assert.assertTrue(getOutput().contains("1"));
      resetOutput();
      getShell().execute("cmdscope");
      Assert.assertTrue(getOutput().contains("1"));
   }
}
