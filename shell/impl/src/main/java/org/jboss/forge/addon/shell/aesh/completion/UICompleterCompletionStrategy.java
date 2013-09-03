package org.jboss.forge.addon.shell.aesh.completion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.aesh.cl.completer.CompleterData;
import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.parser.Parser;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.ManyValued;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * Completes the Aesh {@link Completion} object with values from the {@link UICompleter}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class UICompleterCompletionStrategy implements OptionCompleter
{
   private final OptionCompleter fallback;
   private final InputComponent<?, Object> input;
   private final ConverterFactory converterFactory;
   private final ShellContext context;

   public UICompleterCompletionStrategy(OptionCompleter fallback, ShellContext context,
            InputComponent<?, Object> input,
            ConverterFactory converterFactory)
   {
      this.fallback = fallback;
      this.context = context;
      this.input = input;
      this.converterFactory = converterFactory;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public void complete(final CompleterData completerData)
   {
      String completeValue = completerData.getGivenCompleteValue();
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
         for (Object proposal : completer.getCompletionProposals(context, input, completeValue))
         {
            if (proposal != null)
            {
               String convertedValue = converter.convert(proposal);
               choices.add(convertedValue);
            }
         }
         // Remove already set values in many valued components
         if (input instanceof ManyValued)
         {
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
         }
         if (choices.size() > 1)
         {
            String startsWith = Parser.findStartsWith(choices);
            if (startsWith.length() > completeValue.length())
            {
               String substring = startsWith.substring(completeValue.length());
               completerData.addCompleterValue(Parser.switchSpacesToEscapedSpacesInWord(substring));
               completerData.setAppendSpace(false);
            }
            else
            {
               for (String choice : choices)
               {
                  if (completeValue.isEmpty() || choice.startsWith(completeValue))
                  {
                     completerData.addCompleterValue(Parser.switchSpacesToEscapedSpacesInWord(choice));
                  }
               }
            }
         }
         else if (choices.size() == 1)
         {
            String candidate = choices.get(0).substring(completeValue.length());
            completerData.addCompleterValue(Parser.switchSpacesToEscapedSpacesInWord(candidate));
         }
      }
      else
      {
         // fallback to the other completion strategy
         if (fallback != null)
         {
            fallback.complete(completerData);
         }
      }
   }
}
