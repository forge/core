/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins.builtin;

import java.io.File;

import org.jboss.forge.shell.exceptions.NoSuchCommandException;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class RunPluginTest extends AbstractShellTest
{

   @Test(expected = NoSuchCommandException.class)
   public void testRun() throws Exception
   {
      File file = new File("src/test/resources/org/jboss/forge/shell/test/plugins/builtin/RunPluginTest.fsh");
      Assert.assertTrue("Script file does not exist", file.exists());
      getShell().execute(file);
   }

}
