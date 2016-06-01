/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.NavigationResult;

/**
 * Provides pre-execution steps based on the current {@link UIContext}.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface PrerequisiteCommandsProvider
{
   /**
    * Implementations should return the steps required to run before this command
    * 
    * @param context the current {@link UIContext}
    * @return the {@link NavigationResult} to be displayed. The command that originated it will be automatically
    *         appended to the end of the {@link NavigationResult} returned
    */
   NavigationResult getPrerequisiteCommands(UIContext context);
}
