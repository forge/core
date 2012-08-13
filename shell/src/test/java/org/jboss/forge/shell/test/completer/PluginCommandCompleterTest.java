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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.Root;
import org.jboss.forge.shell.completer.PluginCommandCompleter;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class PluginCommandCompleterTest
{

   @Deployment
   public static JavaArchive getDeployment()
   {

      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addPackages(true, Root.class.getPackage())
               .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private PluginCommandCompleter completer;

   @Test
   public void testCompleteNothing() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(0, index);
      assertTrue(candidates.contains("mockcompleterplugin "));
      assertTrue(candidates.contains("mockcompleterplugin2 "));
   }

   @Test
   public void testPartialCompletion() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mock";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(0, index);
      assertTrue(candidates.contains("mockcompleterplugin "));
   }

   @Test
   public void testFullPluginCompletionAddsSpace() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin2";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(0, index);
      assertTrue(candidates.contains("mockcompleterplugin2 "));
   }

   @Test
   public void testDefaultCommandCompletion() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin2 ";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(input.length(), index);
      assertTrue(candidates.isEmpty());
   }

   @Test
   public void testCommandCompletion() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin ";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(input.length(), index);
      assertTrue(candidates.contains("command1 "));
      assertTrue(candidates.contains("command2 "));
   }

   @Test
   public void testPartialCommandCompletion() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin comm";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(input.indexOf("comm"), index);
      assertTrue(candidates.contains("command1 "));
      assertTrue(candidates.contains("command2 "));
   }

   @Test
   public void testRequiredNamedOptionCompletion() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin command2 ";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(input.length(), index);
      assertTrue(candidates.contains("--option "));
   }

   @Test
   public void testRequiredNamedOptionAutoSelectedFromMultipleNamedOptions() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin3 ";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(input.length(), index);
      assertEquals(1, candidates.size());
      assertTrue(candidates.contains("--two "));
   }

   @Test
   public void testEnumCompletion() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin command4 --option ";

      int index = completer.complete(input, input.length(), candidates);
      assertEquals(5, candidates.size());
      assertEquals(input.length(), index);
   }

   @Test
   public void testRequiredNamedOptionCompletionAppendsSpaceOnPartial() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin command3 --option foo";

      int index = completer.complete(input, input.length(), candidates);
      assertTrue(candidates.isEmpty());
      assertEquals(input.length(), index);
   }

   @Test
   public void testRequiredNamedOptionCompletionTabTwice() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin command3 --option foo ";

      int index = completer.complete(input, input.length(), candidates);
      assertEquals(input.length(), index);
      assertEquals(1, candidates.size());
      assertTrue(candidates.contains("--option2 "));
   }

   @Test
   public void testRequiredMultipleNamedOptionCompletion() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin command3 --";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(2, candidates.size());
      assertTrue(candidates.contains("--option "));
      assertTrue(candidates.contains("--option2 "));
      assertEquals(input.length() - 2, index);
   }

   @Test
   public void testRequiredMultipleNamedOptionCompletionWithSpace() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin command3 -- ";
      int index = completer.complete(input, input.length(), candidates);

      // Only suggests one option here because it is required and we do not have a partial token.
      assertEquals(1, candidates.size());
      assertTrue(candidates.contains("--option "));
      assertEquals(input.length(), index);
   }

   @Test
   public void testCustomCompleterValue() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin command3 --option2 B";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(2, candidates.size());
      assertTrue(candidates.contains("Bar"));
      assertTrue(candidates.contains("Baz"));
      assertEquals(input.length() - 1, index);
   }

   @Test
   public void testCustomCompleterUnvalued() throws Exception
   {
      ArrayList<CharSequence> candidates = new ArrayList<CharSequence>();
      String input = "mockcompleterplugin command3 --option2 ";
      int index = completer.complete(input, input.length(), candidates);
      assertEquals(5, candidates.size());
      assertTrue(candidates.contains("Foo"));
      assertTrue(candidates.contains("Bar"));
      assertTrue(candidates.contains("Baz"));
      assertTrue(candidates.contains("Cal"));
      assertTrue(candidates.contains("Cav"));
      assertEquals(input.length(), index);
   }

}
