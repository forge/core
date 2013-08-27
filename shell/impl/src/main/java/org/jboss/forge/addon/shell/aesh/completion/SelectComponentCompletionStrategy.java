/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.completion;

import java.util.ArrayList;
import java.util.List;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.parser.Parser;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * 
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class SelectComponentCompletionStrategy implements CompletionStrategy
{

   @SuppressWarnings("unchecked")
   @Override
   public void complete(final CompleteOperation completeOperation, final InputComponent<?, Object> input,
            final ShellContext context,
            final String typedValue, final ConverterFactory converterFactory)
   {
      SelectComponent<?, Object> selectComponent = (SelectComponent<?, Object>) input;
      Converter<Object, String> itemLabelConverter = (Converter<Object, String>) InputComponents
               .getItemLabelConverter(converterFactory, selectComponent);
      Iterable<Object> valueChoices = selectComponent.getValueChoices();
      List<String> choices = new ArrayList<String>();
      for (Object choice : valueChoices)
      {
         String convert = itemLabelConverter.convert(choice);
         if (convert != null)
         {
            choices.add(convert);
         }
      }
      // Copied from FileLister
      // TODO Review
      if (choices.size() > 1)
      {
         String startsWith = Parser.findStartsWith(choices);
         if (startsWith != null && startsWith.length() > 0 &&
                  startsWith.length() > typedValue.length())
         {
            String substring = startsWith.substring(typedValue.length());
            String candidate = Parser.switchSpacesToEscapedSpacesInWord(substring);
            completeOperation.addCompletionCandidate(candidate);
            completeOperation.setOffset(completeOperation.getCursor() - typedValue.length());
         }
         else
         {
            completeOperation.addCompletionCandidates(choices);
            if (!completeOperation.getCompletionCandidates().isEmpty() && !typedValue.isEmpty())
            {
               completeOperation.setOffset(completeOperation.getCursor() - typedValue.length());
            }
         }
      }
      else if (choices.size() == 1)
      {
         completeOperation.addCompletionCandidate(choices.get(0).substring(typedValue.length()));
         if (!completeOperation.getCompletionCandidates().isEmpty() && !typedValue.isEmpty())
         {
            completeOperation.setOffset(completeOperation.getCursor() - typedValue.length());
         }
      }

   }
}
