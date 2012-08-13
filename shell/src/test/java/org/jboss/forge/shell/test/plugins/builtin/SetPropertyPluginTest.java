/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class SetPropertyPluginTest extends AbstractShellTest
{
   @Test
   public void testExecuteCommand() throws Exception
   {
      getShell().setVerbose(true);
      assertTrue(getShell().isVerbose());
      getShell().execute("set VERBOSE false");
      assertFalse(getShell().isVerbose());
      getShell().setVerbose(true);
   }
}
