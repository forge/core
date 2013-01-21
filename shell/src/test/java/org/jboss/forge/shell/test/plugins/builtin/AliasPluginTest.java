/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.junit.Assert.fail;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.exceptions.NoSuchCommandException;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AliasPluginTest extends AbstractShellTest
{
   @Test(expected = NoSuchCommandException.class)
   public void testCreateAlias() throws Exception
   {
      /*
       * Nobody home yet
       */
      try
      {
         getShell().execute("doohickey");
         fail();
      }
      catch (Exception e)
      {
      }

      getShell().execute("alias \"doohickey=echo hello\"");

      /*
       * It should work now
       */
      try
      {
         getShell().execute("doohickey");
      }
      catch (Exception e)
      {
         fail();
      }

      getShell().execute("unalias doohickey");

      /*
       * Now we should fail again
       */
      getShell().execute("doohickey");
   }

   @Test
   public void testCreateAliasWithSpaces() throws Exception
   {
      getShell().execute("alias \"doohickey =echo hello\"");

      getShell().execute("doohickey");
      Assert.assertFalse(getOutput().contains("echo hello"));
      Assert.assertTrue(getOutput().contains("hello"));
   }

}
