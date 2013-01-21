/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import javax.inject.Inject;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class WaitTest extends AbstractShellTest
{
   @Inject
   private Wait wait;

   @Inject
   private Shell shell;

   @Test
   public void testWaitCompletesAfterCommand() throws Exception
   {
      Assert.assertFalse(wait.isWaiting());
      wait.start();
      Assert.assertTrue(wait.isWaiting());
      shell.execute("echo foo");
      Assert.assertFalse(wait.isWaiting());
   }

   @Test
   public void testWait() throws Exception
   {
      Assert.assertFalse(wait.isWaiting());
      wait.start();
      Assert.assertTrue(wait.isWaiting());
      Thread.sleep(1000);
      wait.stop();
      Assert.assertFalse(wait.isWaiting());
   }
}
