/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.wizard;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.result.NavigationResult;

/**
 * An {@link UICommand} that supports multiple steps.
 * 
 * Eg: Next, Previous buttons are enabled in Eclipse
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface UIWizard extends UICommand
{
   /**
    * Returns the next {@link UIWizardStep}, or null if this is the last {@link UICommand} in the flow.
    */
   default NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }
}
