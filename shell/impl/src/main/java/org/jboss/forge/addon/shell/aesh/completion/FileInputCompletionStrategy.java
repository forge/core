/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.util.FileLister;
import org.jboss.aesh.util.FileLister.Filter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

enum FileInputCompletionStrategy implements CompletionStrategy
{
   DIRECTORY(Filter.DIRECTORY), FILE(Filter.FILE), ALL(Filter.ALL);

   private final Filter filter;

   private FileInputCompletionStrategy(Filter filter)
   {
      this.filter = filter;
   }

   @Override
   public void complete(CompleteOperation completeOperation, InputComponent<?, Object> input, ShellContext context,
            String typedValue, ConverterFactory converterFactory)
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
         cwd = new File(value.toString());
      }
      FileLister fileLister = new FileLister(typedValue == null ? ""
               : Parser.switchEscapedSpacesToSpacesInWord(typedValue), cwd, filter);
      fileLister.findMatchingDirectories(completeOperation);
   }
}