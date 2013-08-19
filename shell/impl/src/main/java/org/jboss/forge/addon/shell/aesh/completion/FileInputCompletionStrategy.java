package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.util.FileLister;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

class FileInputCompletionStrategy implements CompletionStrategy
{

   private final boolean directory;

   public FileInputCompletionStrategy(boolean directory)
   {
      this.directory = directory;
   }

   @Override
   public void complete(CompleteOperation completeOperation, InputComponent<?, Object> input, ShellContext context,
            String typedValue)
   {
      completeOperation.setOffset(completeOperation.getCursor());

      final File cwd;
      Object value = InputComponents.getValueFor(input);
      if (value == null)
      {
         UISelection<FileResource<?>> selection = context.getInitialSelection();
         cwd = selection.isEmpty() ? OperatingSystemUtils.getUserHomeDir() : selection.get()
                  .getUnderlyingResourceObject();
      }
      else
      {
         // TODO: Use ConverterFactory ?
         cwd = new File(value.toString());
      }
      FileLister fileLister = new FileLister(typedValue == null ? "" : typedValue, cwd,
               directory ? FileLister.Filter.DIRECTORY
                        : FileLister.Filter.ALL);
      fileLister.findMatchingDirectories(completeOperation);
   }
}