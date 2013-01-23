/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NumberEntryShellPromptMultiSelectTest extends AbstractShellPromptMultiSelectTest
{

   @Override
   protected <T> Iterable<String> getChoices(final Set<T> options, final Set<T> selected)
   {
      final List<T> optionList = new ArrayList<T>(options);
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
                  final int index = optionList.indexOf(t);
                  if (index >= 0)
                  {
                     optionList.remove(index);
                     return "0" + Integer.toString(index + 1);
                  }
                  throw new IllegalStateException();
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
      return "0" + Integer.toString(options.size() + 1);
   }

}
