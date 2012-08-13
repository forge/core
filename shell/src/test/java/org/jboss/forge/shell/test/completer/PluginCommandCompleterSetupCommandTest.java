/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.completer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.completer.PluginCommandCompleter;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class PluginCommandCompleterSetupCommandTest extends AbstractShellTest
{

   @Inject
   private PluginCommandCompleter completer;

   @Test
   public void testCompletesSetupCommand() throws Exception
   {
      initializeJavaProject();

      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin4 set";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(1, candidates.size());
      assertTrue(candidates.contains("setup "));
      assertEquals(input.length() - 3, index);
   }

   @Test
   public void testDoesNotCompleteSetupCommandWhenNoProjectAndProjectRequired() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());

      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin4 set";
      completer.complete(input, input.length(), candidates);
      assertEquals(0, candidates.size());
   }

   @Test
   public void testCompletesUnavailablePluginWithSetupCommand() throws Exception
   {
      initializeJavaProject();

      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplug";
      completer.complete(input, input.length(), candidates);
      assertEquals(4, candidates.size());
   }

}
