/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.progress.DefaultUIProgressMonitor;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

/**
 * Creates UI objects. Should be implemented by UI Providers
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIRuntime
{
   /**
    * Creates a new {@link UIProgressMonitor}
    */
   default UIProgressMonitor createProgressMonitor(UIContext context)
   {
      return new DefaultUIProgressMonitor();
   }

   /**
    * Create a new {@link UIPrompt}
    */
   UIPrompt createPrompt(UIContext context);
}
