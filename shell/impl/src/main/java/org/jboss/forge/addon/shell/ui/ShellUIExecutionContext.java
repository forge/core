/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import org.jboss.forge.addon.ui.DefaultUIProgressMonitor;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIExecutionContext;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUIExecutionContext implements UIExecutionContext
{
   private final ShellContext context;
   private final UIProgressMonitor progressMonitor;

   public ShellUIExecutionContext(ShellContext context)
   {
      this.context = context;
      this.progressMonitor = new DefaultUIProgressMonitor();
   }

   @Override
   public ShellContext getUIContext()
   {
      return context;
   }

   @Override
   public UIProgressMonitor getProgressMonitor()
   {
      return progressMonitor;
   }
}
