/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.furnace.util.Strings;

/**
 * Creates {@link UICompleter} implementations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class Completers
{
   /**
    * @param values the {@link Iterable} of {@link String} values
    * @return an {@link UICompleter} for an {@link Iterable} set of {@link String} values
    */
   public static UICompleter<String> fromValues(Iterable<String> values)
   {
      return new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input,
                  String value)
         {
            Set<String> result = new LinkedHashSet<>();
            for (String item : values)
            {
               if (Strings.isNullOrEmpty(value) || item.startsWith(value))
               {
                  result.add(item);
               }
            }
            return result;
         }
      };
   }
}
