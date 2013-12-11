/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.spi;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;

/**
 * Creates UI objects. Should be implemented by UI Providers
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIContextFactory
{
   /**
    * Creates the {@link UIContext} used
    */
   UIContext createUIContext();

   /**
    * Creates a new {@link UIValidationContext}
    */
   UIValidationContext createUIValidationContext(UIContext context);

   /**
    * Creates a new {@link UIExecutionContext}
    */
   UIExecutionContext createUIExecutionContext(UIContext context);
}
