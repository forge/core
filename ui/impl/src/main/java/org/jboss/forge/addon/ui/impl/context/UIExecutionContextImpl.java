/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.context;

import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;

/**
 * Implementation of the {@link UIExecutionContext} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIExecutionContextImpl implements UIExecutionContext
{

   private final UIContext context;
   private final UIProgressMonitor progressMonitor;

   public UIExecutionContextImpl(UIContext context, UIProgressMonitor progressMonitor)
   {
      this.context = context;
      this.progressMonitor = progressMonitor;
   }

   @Override
   public UIContext getUIContext()
   {
      return context;
   }

   @Override
   public UIProgressMonitor getProgressMonitor()
   {
      return progressMonitor;
   }

}
