package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * Typed completion
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface CompletionStrategy
{
   /**
    * Invoked when an autocomplete for a specific component is required
    * 
    * @param completeOperation
    * @param input
    * @param context
    * @param typedValue
    * @param converterFactory used in conversion operations
    */
   public void complete(CompleteOperation completeOperation, InputComponent<?, Object> input, ShellContext context,
            String typedValue, ConverterFactory converterFactory);
}
