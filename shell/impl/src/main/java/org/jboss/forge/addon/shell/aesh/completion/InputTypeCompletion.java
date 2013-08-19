package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * Typed completion
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface InputTypeCompletion
{
   public void complete(ShellContext context, InputComponent<?, Object> input, CompleteOperation completeOperation);
}
