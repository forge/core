package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.util.InputComponents;

class DefaultInputCompletionStrategy implements CompletionStrategy
{
   @Override
   public void complete(CompleteOperation completeOperation, InputComponent<?, Object> input, ShellContext context,
            String typedValue, ConverterFactory converterFactory)
   {
      UICompleter<Object> completer = InputComponents.getCompleterFor(input);
      if (completer != null)
      {
         for (String proposal : completer.getCompletionProposals(context, input, typedValue))
         {
            completeOperation.addCompletionCandidate(proposal);
         }
      }
      else
      {

      }
   }
}
