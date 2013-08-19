package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.util.FileLister;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
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
      case DIRECTORY_PICKER:
         return DirectoryCompletion.INSTANCE;
      default:
         return null;
      }
   }

   private enum DirectoryCompletion implements InputTypeCompletion
   {
      INSTANCE;

      @Override
      public void complete(ShellContext context, InputComponent<?, Object> input, CompleteOperation completeOperation)
      {
         UISelection<FileResource<?>> selection = context.getInitialSelection();
         File cwd = selection.isEmpty() ? OperatingSystemUtils.getUserHomeDir() : selection.get()
                  .getUnderlyingResourceObject();
         FileLister fileLister = new FileLister("", cwd, FileLister.Filter.DIRECTORY);
         fileLister.findMatchingDirectories(completeOperation);
      }
   }

   private enum FileCompletion implements InputTypeCompletion
   {
      INSTANCE;

      @Override
      public void complete(ShellContext context, InputComponent<?, Object> input, CompleteOperation completeOperation)
      {
         UISelection<FileResource<?>> selection = context.getInitialSelection();
         File cwd = selection.isEmpty() ? OperatingSystemUtils.getUserHomeDir() : selection.get()
                  .getUnderlyingResourceObject();
         FileLister fileLister = new FileLister("", cwd);
         fileLister.findMatchingDirectories(completeOperation);
      }
   }
}
