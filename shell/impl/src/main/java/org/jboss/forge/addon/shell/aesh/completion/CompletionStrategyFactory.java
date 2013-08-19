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
      boolean isUISelect = (component instanceof SelectComponent);
      InputType inputType = component.getFacet(HintsFacet.class).getInputType();
      final CompletionStrategy strategy;
      if (inputType == InputType.FILE_PICKER)
      {
         strategy = new FileInputCompletionStrategy(false);
      }
      else if (inputType == InputType.DIRECTORY_PICKER)
      {
         strategy = new FileInputCompletionStrategy(true);
      }
      else if (isUISelect)
      {
         strategy = new SelectComponentCompletionStrategy();
      }
      else
      {
         strategy = new DefaultInputCompletionStrategy();
      }
      return strategy;
   }
}
