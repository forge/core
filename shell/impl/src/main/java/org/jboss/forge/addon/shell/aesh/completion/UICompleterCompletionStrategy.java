package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Strings;

/**
 * Completes the Aesh {@link Completion} object with values from the {@link UICompleter}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class UICompleterCompletionStrategy implements CompletionStrategy
{

   public CompletionStrategy fallback;

   public UICompleterCompletionStrategy(CompletionStrategy fallback)
   {
      this.fallback = fallback;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void complete(CompleteOperation completeOperation, InputComponent<?, Object> input, ShellContext context,
            String typedValue, ConverterFactory converterFactory)
   {
      UICompleter<Object> completer = InputComponents.getCompleterFor(input);
      if (completer != null)
      {
         final Converter<Object, String> converter;
         if (input instanceof SelectComponent)
         {
            converter = (Converter<Object, String>) InputComponents.getItemLabelConverter(converterFactory,
                     (SelectComponent<?, ?>) input);
         }
         else
         {
            converter = converterFactory.getConverter(input.getValueType(), String.class);
         }

         for (Object proposal : completer.getCompletionProposals(context, input, typedValue))
         {
            if (proposal != null)
            {
               String convertedValue = converter.convert(proposal);
               completeOperation.addCompletionCandidate(convertedValue);
            }
         }
         if (!completeOperation.getCompletionCandidates().isEmpty() && !Strings.isNullOrEmpty(typedValue))
         {
            completeOperation.setOffset(completeOperation.getCursor() - typedValue.length());
         }
      }
      else
      {
         // fallback to the other completion strategy
         if (fallback != null)
         {
            fallback.complete(completeOperation, input, context, typedValue, converterFactory);
         }
      }
   }
}
