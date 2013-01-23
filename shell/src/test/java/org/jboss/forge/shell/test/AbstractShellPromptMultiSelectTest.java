/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.test;

import static org.junit.Assert.assertEquals;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public abstract class AbstractShellPromptMultiSelectTest extends AbstractShellTest
{
   private static final String WILDCARD = "*";

   public enum Stooge
   {
      MOE("Moe Howard"), LARRY("Larry Fine"), CURLY("Curly Howard"), SHEMP("Shemp Howard");

      private final String fullName;

      private Stooge(String fullName)
      {
         this.fullName = fullName;
      }

      public String toString()
      {
         return fullName;
      }
   }

   protected abstract <T> Iterable<String> getChoices(Set<T> options, Set<T> selected);

   protected abstract String getWildcardChoice(String wildcard, Set<?> options);

   private <T> void selectFrom(Set<T> options, Set<T> selected)
   {
      for (String choice : getChoices(options, selected))
      {
         queueInputLines(choice);
      }
      if (!selected.containsAll(options))
      {
         queueInputLines("");
      }
   }

   @Test
   public void testMultiSelectVarargs()
   {
      final Set<String> options = setOf("foo", "bar", "baz");
      selectFrom(options, options);
      assertEquals(options, getShell().promptMultiSelect("blah", "foo", "bar", "baz"));
   }

   @Test
   public void testMultiSelectSomeVarargs()
   {
      final Set<String> options = setOf("foo", "bar", "baz");
      final Set<String> selected = setOf("baz", "foo");
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelect("blah", "foo", "bar", "baz"));
   }

   @Test
   public void testMultiSelectWithWildcardVarargs()
   {
      final Set<String> options = setOf("foo", "bar", "baz");
      queueInputLines(getWildcardChoice(WILDCARD, options));
      assertEquals(options, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", "foo", "bar", "baz"));
   }

   @Test
   public void testMultiSelectWithWildcardSomeVarargs()
   {
      final Set<String> options = setOf("foo", "bar", "baz");
      final Set<String> selected = setOf("baz", "foo");
      selectFrom(options, selected);
      assertEquals(selected,
               getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", "foo", "bar", "baz"));
   }

   @Test
   public void testMultiSelectIntegerVarargs()
   {
      final Set<Integer> options = setOf(2, 4, 6, 8);
      final Set<Integer> selected = setOf(4, 8, 6, 2);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelect("blah", 2, 4, 6, 8));
   }

   @Test
   public void testMultiSelectSomeIntegerVarargs()
   {
      final Set<Integer> options = setOf(2, 4, 6, 8);
      final Set<Integer> selected = setOf(4, 6);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelect("blah", 2, 4, 6, 8));
   }

   @Test
   public void testMultiSelectWithWildcardIntegerVarargs()
   {
      final Set<Integer> options = setOf(2, 4, 6, 8);
      queueInputLines(getWildcardChoice(WILDCARD, options));
      assertEquals(options, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", 2, 4, 6, 8));
   }

   @Test
   public void testMultiSelectWithWildcardSomeIntegerVarargs()
   {
      final Set<Integer> options = setOf(2, 4, 6, 8);
      final Set<Integer> selected = setOf(4, 6);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", 2, 4, 6, 8));
   }

   @Test
   public void testMultiSelectEnumVarargs()
   {
      final Set<Stooge> options = EnumSet.allOf(Stooge.class);
      final Set<Stooge> selected = EnumSet.of(Stooge.SHEMP, Stooge.CURLY, Stooge.MOE, Stooge.LARRY);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelect("blah", Stooge.values()));
   }

   @Test
   public void testMultiSelectSomeEnumVarargs()
   {
      final Set<Stooge> options = EnumSet.allOf(Stooge.class);
      final Set<Stooge> selected = EnumSet.of(Stooge.CURLY, Stooge.LARRY);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelect("blah", Stooge.values()));
   }

   @Test
   public void testMultiSelectWithWildcardEnumVarargs()
   {
      final Set<Stooge> options = EnumSet.allOf(Stooge.class);
      queueInputLines(getWildcardChoice(WILDCARD, options));
      assertEquals(options, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", Stooge.values()));
   }

   @Test
   public void testMultiSelectWithWildcardSomeEnumVarargs()
   {
      final Set<Stooge> options = EnumSet.allOf(Stooge.class);
      final Set<Stooge> selected = EnumSet.of(Stooge.CURLY, Stooge.LARRY);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", Stooge.values()));
   }

   @Test
   public void testMultiSelectFromSet()
   {
      final Set<String> options = setOf("foo", "bar", "baz");
      selectFrom(options, options);
      assertEquals(options, getShell().promptMultiSelect("blah", options));
   }

   @Test
   public void testMultiSelectSomeFromSet()
   {
      final Set<String> options = setOf("foo", "bar", "baz");
      final Set<String> selected = setOf("foo", "baz");
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelect("blah", options));
   }

   @Test
   public void testMultiSelectWithWildcardFromSet()
   {
      final Set<String> options = setOf("foo", "bar", "baz");
      queueInputLines(getWildcardChoice(WILDCARD, options));
      assertEquals(options, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", options));
   }

   @Test
   public void testMultiSelectWithWildcardSomeFromSet()
   {
      final Set<String> options = setOf("foo", "bar", "baz");
      final Set<String> selected = setOf("foo", "baz");
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", options));
   }

   @Test
   public void testMultiSelectFromEnumSet()
   {
      final Set<Stooge> options = EnumSet.allOf(Stooge.class);
      final Set<Stooge> selected = EnumSet.of(Stooge.SHEMP, Stooge.CURLY, Stooge.MOE, Stooge.LARRY);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelect("blah", options));
   }

   @Test
   public void testMultiSelectSomeFromEnumSet()
   {
      final Set<Stooge> options = EnumSet.allOf(Stooge.class);
      final Set<Stooge> selected = EnumSet.of(Stooge.CURLY, Stooge.LARRY);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelect("blah", options));
   }

   @Test
   public void testMultiSelectWithWildcardFromEnumSet()
   {
      final Set<Stooge> options = EnumSet.allOf(Stooge.class);
      queueInputLines(getWildcardChoice(WILDCARD, options));
      assertEquals(options, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", options));
   }

   @Test
   public void testMultiSelectWithWildcardSomeFromEnumSet()
   {
      final Set<Stooge> options = EnumSet.allOf(Stooge.class);
      final Set<Stooge> selected = EnumSet.of(Stooge.CURLY, Stooge.LARRY);
      selectFrom(options, selected);
      assertEquals(selected, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", options));
   }

   @Test
   public void testMultiSelectFromMap()
   {
      final Map<String, Stooge> options = stoogeMap();
      final Set<Stooge> selected = setOf(Stooge.SHEMP, Stooge.CURLY, Stooge.MOE, Stooge.LARRY);
      selectFrom(options.keySet(), selectedKeys(options, selected));
      assertEquals(selected, getShell().promptMultiSelect("blah", options));
   }

   @Test
   public void testMultiSelectSomeFromMap()
   {
      final Map<String, Stooge> options = stoogeMap();
      final Set<Stooge> selected = setOf(Stooge.CURLY, Stooge.LARRY);
      selectFrom(options.keySet(), selectedKeys(options, selected));
      assertEquals(selected, getShell().promptMultiSelect("blah", options));
   }

   @Test
   public void testMultiSelectWithWildcardFromMap()
   {
      final Map<String, Stooge> options = stoogeMap();
      queueInputLines(getWildcardChoice(WILDCARD, options.keySet()));
      assertEquals(EnumSet.allOf(Stooge.class),
               getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", options));
   }

   @Test
   public void testMultiSelectWithWildcardSomeFromMap()
   {
      final Map<String, Stooge> options = stoogeMap();
      final Set<Stooge> selected = setOf(Stooge.CURLY, Stooge.LARRY);
      selectFrom(options.keySet(), selectedKeys(options, selected));
      assertEquals(selected, getShell().promptMultiSelectWithWildcard(WILDCARD, "blah", options));
   }

   private Map<String, Stooge> stoogeMap()
   {
      final Map<String, Stooge> result = new LinkedHashMap<String, AbstractShellPromptMultiSelectTest.Stooge>();
      for (Stooge stooge : Stooge.values())
      {
         result.put(stooge.toString(), stooge);
      }
      return result;
   }

   private <T> Set<T> setOf(T... t)
   {
      final LinkedHashSet<T> result = new LinkedHashSet<T>();
      Collections.addAll(result, t);
      return result;
   }

   private <T> Set<String> selectedKeys(final Map<String, T> options, final Set<T> selected)
   {
      return new AbstractSet<String>()
      {

         @Override
         public Iterator<String> iterator()
         {
            final Iterator<T> wrapped = selected.iterator();
            return new Iterator<String>()
            {

               @Override
               public boolean hasNext()
               {
                  return wrapped.hasNext();
               }

               @Override
               public String next()
               {
                  final T t = wrapped.next();
                  for (Map.Entry<String, T> e : options.entrySet())
                  {
                     if (t.equals(e.getValue()))
                     {
                        return e.getKey();
                     }
                  }
                  throw new IllegalStateException();
               }

               @Override
               public void remove()
               {
                  wrapped.remove();
               }

            };
         }

         @Override
         public int size()
         {
            return selected.size();
         }
      };
   }
}
