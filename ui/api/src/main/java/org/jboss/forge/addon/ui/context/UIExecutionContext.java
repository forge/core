/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.context;

import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

/**
 * A {@link UIExecutionContext} is created when the execution phase is requested
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIExecutionContext extends UIContextProvider
{
   /**
    * Returns the {@link UIProgressMonitor} for this execution
    */
   UIProgressMonitor getProgressMonitor();

   /**
    * Returns the object used to prompt for messages during a UI interaction
    */
   UIPrompt getPrompt();

}
