/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.aesh.line.CommandLineImpl;
import org.jboss.forge.addon.shell.line.CommandLine;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;

/**
 * Implementation of {@link UIContext}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellContextImpl extends AbstractUIContext implements ShellContext
{
   private final Shell shell;
   private final UISelection<?> initialSelection;
   private final Iterable<UIContextListener> listeners;

   public ShellContextImpl(Shell shell, UISelection<?> initialSelection, Iterable<UIContextListener> listeners)
   {
      this.shell = shell;
      this.initialSelection = initialSelection;
      this.listeners = listeners;
      for (UIContextListener listener : listeners)
      {
         listener.contextInitialized(this);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public UISelection<?> getInitialSelection()
   {
      return initialSelection;
   }

   @Override
   public Shell getProvider()
   {
      return shell;
   }

   @Override
   public void close()
   {
      super.close();
      for (UIContextListener listener : listeners)
      {
         listener.contextDestroyed(this);
      }
   }

   @Override
   public boolean isInteractive()
   {
      String sysProp = System.getProperty("INTERACTIVE");
      if (sysProp != null)
      {
         return Boolean.parseBoolean(sysProp);
      }
      Object interactiveFlag = getAttributeMap().get("INTERACTIVE");
      return (interactiveFlag == null || "true".equalsIgnoreCase(interactiveFlag.toString()));
   }

   @Override
   public boolean isVerbose()
   {
      Object verboseFlag = getAttributeMap().get("VERBOSE");
      return (verboseFlag != null && "true".equalsIgnoreCase(verboseFlag.toString()));
   }

   @Override
   public CommandLine getCommandLine()
   {
      org.jboss.aesh.cl.CommandLine<?> cmdLine = (org.jboss.aesh.cl.CommandLine<?>) getAttributeMap()
               .get(org.jboss.aesh.cl.CommandLine.class);
      return new CommandLineImpl(cmdLine);
   }
}
