/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class NameEntryShellPromptMultiSelectTest extends AbstractShellPromptMultiSelectTest
{

   @Override
   protected <T> Iterable<String> getChoices(final Set<T> options, final Set<T> selected)
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
                  return transform(wrapped.next());
               }

               @Override
               public void remove()
               {
                  wrapped.remove();
               }

               private String transform(T t)
               {
                  return t instanceof Enum<?> ? ((Enum<?>) t).name() : t.toString();
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

   @Override
   protected String getWildcardChoice(String wildcard, Set<?> options)
   {
      return wildcard;
   }

}
