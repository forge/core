/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.command;

import java.util.Collection;

import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Provides pre-execution steps based on the current {@link UIContext}.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface PreStepsUICommand extends UICommand
{
   /**
    * Implementations should return the steps required to run before this command
    * 
    * @param context the current {@link UIContext}
    * @return the pre execution steps, if any. Must return an empty {@link Collection} or null if no steps are available
    */
   Collection<Class<? extends UICommand>> getPreSteps(UIContext context);
}
