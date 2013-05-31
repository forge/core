/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.observers;

import static org.junit.Assert.assertTrue;

import org.jboss.forge.shell.exceptions.NoSuchCommandException;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class DidYouMeanObserverTest extends AbstractShellTest
{
   private static final String EOL = System.getProperty("line.separator");

   @Test(expected = NoSuchCommandException.class)
   public void testNoSuggestionsSuggestMissingPlugin() throws Exception
   {
      getShell().execute("aninvalidcommand");
   }

   @Test
   public void testSuggestInvalidCommand() throws Exception
   {
      getShell().execute("l");
      assertTrue(getOutput().contains(String.format(
         "Did you mean this ?%s\tls", EOL)));
   }

   @Test
   public void testSuggestInvalidAliasedCommand() throws Exception
   {
      getShell().execute("alias \"ll=ls -l\"");
      getShell().execute("l");
      assertTrue(getOutput().contains(String.format(
         "Did you mean any of these ?%1$s\tll%1$s\tls", EOL)));
   }

}
