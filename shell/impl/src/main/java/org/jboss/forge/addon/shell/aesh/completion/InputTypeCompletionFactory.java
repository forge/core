package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.util.FileLister;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * Returns the completion based on the input type
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class InputTypeCompletionFactory
{
   public InputTypeCompletion getCompletionFor(InputType type)
   {
      switch (type)
      {
      case FILE_PICKER:
         return FileCompletion.INSTANCE;
      default:
         return null;
      }
   }

   private enum FileCompletion implements InputTypeCompletion
   {
      INSTANCE;

      @SuppressWarnings("unchecked")
      @Override
      public void complete(ShellImpl shellImpl, CompleteOperation completeOperation)
      {
         UISelection<FileResource<?>> selection = (UISelection<FileResource<?>>) shellImpl.getCurrentSelection();
         File cwd = selection.isEmpty() ? OperatingSystemUtils.getUserHomeDir() : selection.get()
                  .getUnderlyingResourceObject();
         FileLister fileLister = new FileLister("", cwd);
         fileLister.findMatchingDirectories(completeOperation);
      }
   }
}
