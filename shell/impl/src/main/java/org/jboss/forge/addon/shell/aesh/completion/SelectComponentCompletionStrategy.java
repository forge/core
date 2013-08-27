/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Strings;

/**
 * 
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class SelectComponentCompletionStrategy implements CompletionStrategy
{

   @SuppressWarnings("unchecked")
   @Override
   public void complete(CompleteOperation completeOperation, InputComponent<?, Object> input, ShellContext context,
            String typedValue, ConverterFactory converterFactory)
   {
      SelectComponent<?, Object> selectComponent = (SelectComponent<?, Object>) input;
      Converter<Object, String> itemLabelConverter = (Converter<Object, String>) InputComponents
               .getItemLabelConverter(converterFactory, selectComponent);
      boolean noTypedValue = Strings.isNullOrEmpty(typedValue);
      Iterable<Object> valueChoices = selectComponent.getValueChoices();
      for (Object choice : valueChoices)
      {
         String convert = itemLabelConverter.convert(choice);
         if (noTypedValue || convert.startsWith(typedValue))
         {
            completeOperation.addCompletionCandidate(convert);
         }
      }
      if (!completeOperation.getCompletionCandidates().isEmpty() && !noTypedValue)
      {
         completeOperation.setOffset(completeOperation.getCursor() - typedValue.length());
      }
   }
}
