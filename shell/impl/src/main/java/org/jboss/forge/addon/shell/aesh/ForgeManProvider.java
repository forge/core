/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.jboss.aesh.console.helper.ManProvider;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.ui.UICommand;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ForgeManProvider implements ManProvider
{
   private static final List<String> extensions = Arrays.asList("txt", "ad", "asciidoc");

   private final ShellImpl shell;
   private final CommandManager manager;

   public ForgeManProvider(ShellImpl shell, CommandManager manager)
   {
      this.shell = shell;
      this.manager = manager;
   }

   @Override
   public InputStream getManualDocument(String command)
   {
      for (UICommand cmd : manager.getAllCommands())
      {
         ShellContextImpl context = shell.newShellContext();
         try
         {
            if (command.equals(manager.getCommandName(context, cmd)))
            {
               Class<? extends UICommand> commandType = cmd.getMetadata(context).getType();
               InputStream stream = null;
               for (String ext : extensions)
               {
                  stream = commandType.getClassLoader().getResourceAsStream(
                           commandType.getName().replaceAll("\\.", File.separator) + "." + ext);
                  if (stream != null)
                     return stream;
               }
            }
         }
         finally
         {
            context.destroy();
         }
      }
      return null;
   }
}
