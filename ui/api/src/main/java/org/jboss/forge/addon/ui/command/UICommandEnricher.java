/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.command;

import org.jboss.forge.addon.ui.context.UIContext;

/**
 * A {@link UICommandEnricher} can enrich the original command into another command (add steps)
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UICommandEnricher
{
   /**
    * Called by the UI API when a command is about to be used. Should never return null.
    */
   UICommand enrich(UIContext context, UICommand original);

}
