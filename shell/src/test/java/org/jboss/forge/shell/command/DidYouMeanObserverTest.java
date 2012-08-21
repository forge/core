/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.command;

import static org.junit.Assert.assertTrue;

import org.jboss.forge.shell.exceptions.NoSuchCommandException;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class DidYouMeanObserverTest extends AbstractShellTest
{

   @Test(expected = NoSuchCommandException.class)
   public void testNoSuggestionsSuggestMissingPlugin() throws Exception
   {
      getShell().execute("aninvalidcommand");
   }

   @Test
   public void testSuggestInvalidCommand() throws Exception
   {
      getShell().execute("l");
      assertTrue(getOutput().contains("Did you mean this ?\n\tls"));
   }

   @Test
   public void testSuggestInvalidAliasedCommand() throws Exception
   {
      getShell().execute("alias \"ll=ls -l\"");
      getShell().execute("l");
      assertTrue(getOutput().contains("Did you mean any of these ?\n\tll\n\tls"));
   }

}
