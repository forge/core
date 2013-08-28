package org.jboss.forge.addon.shell.aesh.completion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.parser.Parser;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.util.InputComponents;

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
         List<String> choices = new ArrayList<String>();
         for (Object proposal : completer.getCompletionProposals(context, input, typedValue))
         {
            if (proposal != null)
            {
               String convertedValue = Parser.switchSpacesToEscapedSpacesInWord(converter.convert(proposal));
               choices.add(convertedValue);
            }
         }
         // Remove already set values
         Object value = InputComponents.getValueFor(input);
         if (value != null)
         {
            if (value instanceof Iterable)
            {
               Iterator<Object> it = ((Iterable<Object>) value).iterator();
               while (it.hasNext())
               {
                  Object next = it.next();
                  String convert = converter.convert(next);
                  choices.remove(convert);
               }
            }
            else
            {
               String convert = converter.convert(value);
               choices.remove(convert);
            }
         }

         if (choices.size() > 1)
         {
            String startsWith = Parser.findStartsWith(choices);
            if (startsWith.length() > typedValue.length())
            {
               String substring = startsWith.substring(typedValue.length());
               String candidate = Parser.switchSpacesToEscapedSpacesInWord(substring);
               completeOperation.addCompletionCandidate(candidate);
               completeOperation.setOffset(completeOperation.getCursor());
               completeOperation.doAppendSeparator(false);
            }
            else
            {
               if (typedValue.isEmpty())
               {
                  completeOperation.addCompletionCandidates(choices);
               }
               else
               {
                  for (String choice : choices)
                  {
                     if (choice.startsWith(typedValue))
                     {
                        completeOperation.addCompletionCandidate(choice);
                     }
                  }
               }
               if (!completeOperation.getCompletionCandidates().isEmpty() && !typedValue.isEmpty())
               {
                  completeOperation.setOffset(completeOperation.getCursor() - typedValue.length());
               }
            }
         }
         else if (choices.size() == 1)
         {
            completeOperation.addCompletionCandidate(choices.get(0).substring(typedValue.length()));
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
