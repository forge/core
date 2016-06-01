/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Listens for missing commands in {@link Shell}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface CommandNotFoundListener
{
   /**
    * Called when the command to be executed is not found
    */
   void onCommandNotFound(String line, UIContext context);

}
