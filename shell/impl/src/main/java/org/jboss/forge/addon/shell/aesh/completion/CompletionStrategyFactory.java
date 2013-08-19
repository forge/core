package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;

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
      switch (inputType)
      {
      case FILE_PICKER:
         return new FileInputCompletionStrategy(false);
      case DIRECTORY_PICKER:
         return new FileInputCompletionStrategy(true);
      default:
         return new DefaultInputCompletionStrategy();
      }
   }
}
