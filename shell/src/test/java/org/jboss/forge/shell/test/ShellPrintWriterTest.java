/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test;

import static org.junit.matchers.JUnitMatchers.containsString;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ShellPrintWriterTest extends AbstractShellTest
{
   @Test
   public void testPrintNullDoesNotCrash() throws Exception
   {
      getShell().print(null);
   }

   @Test
   public void testPrintColorNullDoesNotCrash() throws Exception
   {
      getShell().print(null, null);
   }

   @Test
   public void testPrintlnNullDoesNotCrash() throws Exception
   {
      getShell().println(null);
   }

   @Test
   public void testPrintlnColorNullDoesNotCrash() throws Exception
   {
      getShell().println(null, null);
   }

   @Test
   public void testPrintlnVerboseNullDoesNotCrash() throws Exception
   {
      getShell().setVerbose(true);
      getShell().printlnVerbose(null);
   }

   @Test
   public void testPrintlnVerboseColorNullDoesNotCrash() throws Exception
   {
      getShell().setVerbose(true);
      getShell().printlnVerbose(null, null);
   }

   @Test
   public void testRenderColorNullDoesNotCrash() throws Exception
   {
      getShell().renderColor(null, null);
   }

   @Test
   public void testPrintUTF8Chars() throws Exception
   {
      String str = "\u2501 Hello World";
      getShell().println(str);
      Assert.assertThat(getOutput(), containsString(str));
   }

}
