/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.shell.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.exceptions.EndOfStreamException;
import org.jboss.forge.shell.test.completer.MockEnum;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ShellPromptTest extends AbstractShellTest
{
   @Test
   public void testAutoDefault() throws Exception
   {
      getShell().setAcceptDefaults(true);

      // String
      assertEquals("foo", getShell().prompt("Default?", "foo"));

      // Object
      assertEquals(new Integer(10), getShell().prompt("Default?", Integer.class, 10));

      // Boolean
      assertTrue(getShell().promptBoolean("Would you like cake?"));
      assertFalse(getShell().promptBoolean("Would you like cake?", false));

      // List
      List<String> choices = Arrays.asList("blue", "green", "red", "yellow");
      String choice = getShell().promptChoiceTyped("What is your favorite color?", choices, "yellow");
      assertEquals(choices.get(3), choice);

      // Enum
      assertEquals(MockEnum.BAR, getShell().promptEnum("Enummy nine?", MockEnum.class, MockEnum.BAR));

      // Regex
      assertEquals("foo", getShell().promptRegex("Default regex?", ".*", "foo"));

      // Common
      assertEquals("foo", getShell().promptCommon("Default regex?", PromptType.ANY, "foo"));

      // Secret
      assertEquals("foo", getShell().promptSecret("Default regex?", "foo"));

      // File
      assertEquals(getShell().getCurrentDirectory(),
               getShell().promptFile("Default file?", getShell().getCurrentDirectory()));

      getShell().setAcceptDefaults(false);
   }

   @Test
   public void testPromptBoolean() throws Exception
   {
      queueInputLines("y");
      assertTrue(getShell().promptBoolean("Would you like cake?"));

      queueInputLines("yes");
      assertTrue(getShell().promptBoolean("Would you like cake?"));

      queueInputLines("n");
      assertFalse(getShell().promptBoolean("Would you like cake?"));

      queueInputLines("no");
      assertFalse(getShell().promptBoolean("Would you like cake?"));
   }

   @Test
   public void testPromptBooleanDefaultsToYes() throws Exception
   {
      queueInputLines("");
      assertTrue(getShell().promptBoolean("Would you like cake?"));
   }

   @Test
   public void testPromptBooleanLoopsIfBadInput() throws Exception
   {
      queueInputLines("asdfdsf\n \n");
      assertFalse(getShell().promptBoolean("Would you like cake?", false));

      queueInputLines("asdfdsf\n n\n");
      assertFalse(getShell().promptBoolean("Would you like cake?", false));

      queueInputLines("asdfdsf\n y\n");
   }

   @Test
   public void testPromptChoiceList() throws Exception
   {
      List<String> choices = Arrays.asList("blue", "green", "red", "yellow");

      queueInputLines("foo", "2");
      int choice = getShell().promptChoice("What is your favorite color?", choices);
      assertEquals(1, choice);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testPromptChoiceListEmpty() throws Exception
   {
      List<String> choices = Arrays.asList();
      getShell().promptChoice("What is your favorite color?", choices);
      fail();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testPromptChoiceListNull() throws Exception
   {
      List<String> choices = null;
      getShell().promptChoice("What is your favorite color?", choices);
      fail();
   }

   @Test
   public void testPromptChoiceListTyped() throws Exception
   {
      List<String> choices = Arrays.asList("blue", "green", "red", "yellow");

      queueInputLines("foo", "2");
      String choice = getShell().promptChoiceTyped("What is your favorite color?", choices);
      assertEquals(choices.get(1), choice);
   }

   @Test
   public void testPromptChoiceListTypedDefault() throws Exception
   {
      List<String> choices = Arrays.asList("blue", "green", "red", "yellow");

      queueInputLines("foo", "");
      String choice = getShell().promptChoiceTyped("What is your favorite color?", choices, "yellow");
      assertEquals(choices.get(3), choice);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testPromptChoiceTypedListEmpty() throws Exception
   {
      List<String> choices = Arrays.asList();
      getShell().promptChoiceTyped("What is your favorite color?", choices);
      fail();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testPromptChoiceTypedListNull() throws Exception
   {
      List<String> choices = null;
      getShell().promptChoiceTyped("What is your favorite color?", choices);
      fail();
   }

   @Test
   public void testPromptChoiceMapByIndex() throws Exception
   {
      Map<String, String> options = new HashMap<String, String>();
      options.put("blue", "option1");
      options.put("green", "option2");
      options.put("red", "option3");

      queueInputLines("2");
      String choice = getShell().promptChoice("What is your favorite color?", options);
      assertEquals("option1", choice);
   }

   @Test
   public void testPromptChoiceMapByIndexSingleItem() throws Exception
   {
      Map<String, String> options = new HashMap<String, String>();
      options.put("blue", "option1");

      queueInputLines("1");
      String choice = getShell().promptChoice("What is your favorite color?", options);
      assertEquals("option1", choice);
   }

   @Test
   public void testPromptChoiceMapByName() throws Exception
   {
      Map<String, String> options = new HashMap<String, String>();
      options.put("blue", "option1");
      options.put("green", "option2");
      options.put("red", "option3");

      queueInputLines("green");
      String choice = getShell().promptChoice("What is your favorite color?", options);
      assertEquals("option2", choice);
   }

   @Test
   public void testPromptChoiceMapLoopsIfNonExistingIndex() throws Exception
   {
      Map<String, String> options = new HashMap<String, String>();
      options.put("blue", "option1");
      options.put("green", "option2");
      options.put("red", "option3");

      queueInputLines("5", "2");
      String choice = getShell().promptChoice("What is your favorite color?", options);
      assertEquals("option1", choice);
   }

   @Test
   public void testPromptChoiceMapLoopsInvalidInput() throws Exception
   {
      Map<String, String> options = new HashMap<String, String>();
      options.put("blue", "option1");
      options.put("green", "option2");
      options.put("red", "option3");

      queueInputLines("bla", "2");
      String choice = getShell().promptChoice("What is your favorite color?", options);
      assertEquals("option1", choice);
   }

   @Test
   public void testPromptEnum() throws Exception
   {
      queueInputLines("BAR");
      assertEquals(MockEnum.BAR, getShell().promptEnum("Enummy one?", MockEnum.class));
   }

   @Test(expected = EndOfStreamException.class)
   public void testPromptEnumFallbackToListAsksForChoice() throws Exception
   {
      queueInputLines("");
      getShell().promptEnum("Enummy two?", MockEnum.class);
      fail();
   }

   @Test
   public void testPromptEnumFallbackToList() throws Exception
   {
      queueInputLines("not-a-value", "2");
      assertEquals(MockEnum.BAR, getShell().promptEnum("Enummy three?", MockEnum.class));

      queueInputLines("not-a-value", "", "", "", "3");
      assertEquals(MockEnum.BAZ, getShell().promptEnum("Enummy four?", MockEnum.class));

      queueInputLines("not-a-value", "4");
      assertEquals(MockEnum.CAT, getShell().promptEnum("Enummy five?", MockEnum.class));
   }

   @Test
   public void testPromptEnumDefault() throws Exception
   {
      queueInputLines("");
      assertEquals(MockEnum.CAT, getShell().promptEnum("Enummy six?", MockEnum.class, MockEnum.CAT));

      queueInputLines("");
      assertEquals(MockEnum.CAT, getShell().promptEnum("Enummy seven?", MockEnum.class, MockEnum.CAT));
   }

   @Test
   public void testPromptEnumDefaultFallbackToList() throws Exception
   {
      queueInputLines("not-a-value", "");
      assertEquals(MockEnum.BAR, getShell().promptEnum("Enummy eight?", MockEnum.class, MockEnum.BAR));
   }

   @Test
   public void testPromptEnumDefaultFallbackToListWithDefault() throws Exception
   {
      queueInputLines("not-a-value", "eeee", "aaa", "");
      assertEquals(MockEnum.BAR, getShell().promptEnum("Enummy nine?", MockEnum.class, MockEnum.BAR));
   }

   @Test
   public void testPromptSecret() throws Exception
   {
      queueInputLines("secret");
      assertEquals("secret", getShell().promptSecret("What's your secret?"));
   }

   @Test
   public void testPromptSecretDefault() throws Exception
   {
      queueInputLines("secret");
      assertEquals("secret", getShell().promptSecret("What's your secret?", "foo"));

      queueInputLines("");
      assertEquals("foo", getShell().promptSecret("What's your secret?", "foo"));
   }

   @Test
   public void testPromptDefault() throws Exception
   {
      queueInputLines("super");
      assertEquals("super", getShell().prompt("What's your default?", "default"));

      queueInputLines("");
      assertEquals("default", getShell().prompt("What's your default?", "default"));
   }
}
