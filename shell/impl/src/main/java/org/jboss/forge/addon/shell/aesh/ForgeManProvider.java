/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
         try (ShellContextImpl context = shell.createUIContext())
         {
            if (command.equals(manager.getCommandName(context, cmd)))
            {
               URL docLocation = cmd.getMetadata(context).getDocLocation();
               if (docLocation != null)
               {
                  try
                  {
                     return docLocation.openStream();
                  }
                  catch (IOException e)
                  {
                     return null;
                  }
               }
            }
         }
      }
      return null;
   }
}
