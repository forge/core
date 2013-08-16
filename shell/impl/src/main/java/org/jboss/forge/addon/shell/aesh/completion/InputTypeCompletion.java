package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.forge.addon.shell.ShellImpl;

/**
 * Typed completion
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface InputTypeCompletion
{
   public void complete(ShellImpl shellImpl, CompleteOperation completeOperation);
}
