/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.commands;

import java.io.IOException;

import javax.inject.Inject;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCommand;
import org.jboss.aesh.extensions.less.Less;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class LessCommand extends AbstractNativeAeshCommand
{

   @Inject
   @WithAttributes(label = "Arguments", required = true, requiredMessage = "Missing filename (\"less --help\" for help)")
   private UIInputMany<FileResource<?>> arguments;

   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata()
               .name("less")
               .description("less - opposite of more");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      if (arguments.getValue() != null)
      {
         FileResource<?> file = arguments.getValue().iterator().next();
         if (!file.exists())
         {
            validator.addValidationError(arguments, file.getFullyQualifiedName() + " No such file or directory");
         }
         else if (file.isDirectory())
         {
            validator.addValidationError(arguments, file.getFullyQualifiedName() + " is a directory");
         }
      }
   }

   @Override
   public ConsoleCommand getConsoleCommand(ShellContext context) throws IOException
   {
      Console console = context.getProvider().getConsole();
      FileResource<?> file = arguments.getValue().iterator().next();
      Less less = new Less(console);
      less.setFile(file.getUnderlyingResourceObject());
      return less;
   }
}
