package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;

/**
 * Returns the completion based on the input component
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CompletionStrategyFactory
{
   public static CompletionStrategy getCompletionFor(InputComponent<?, Object> component)
   {
      InputType inputType = component.getFacet(HintsFacet.class).getInputType();
      CompletionStrategy strategy = null;
      if (inputType == InputType.FILE_PICKER)
      {
         strategy = FileInputCompletionStrategy.ALL;
      }
      else if (inputType == InputType.DIRECTORY_PICKER)
      {
         strategy = FileInputCompletionStrategy.DIRECTORY;
      }
      else if (inputType == InputType.CHECKBOX || Boolean.class
               .isAssignableFrom(component.getValueType()))
      {
         strategy = NoopCompletionStrategy.INSTANCE;
      }
      else if (component instanceof SelectComponent)
      {
         strategy = SelectComponentCompletionStrategy.INSTANCE;
      }
      // Always try UICompleter first and then fallback to the chosen strategy
      strategy = new UICompleterCompletionStrategy(strategy);
      return strategy;
   }
}
