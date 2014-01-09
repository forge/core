/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.spi;

import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIPrompt;

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
   UIProgressMonitor createProgressMonitor(UIContext context);

   /**
    * Create a new {@link UIPrompt}
    */
   UIPrompt createPrompt(UIContext context);
}
