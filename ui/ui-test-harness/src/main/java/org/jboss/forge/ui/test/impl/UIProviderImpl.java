/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIProviderImpl implements UIProvider
{
   private boolean graphical;

   public UIProviderImpl(boolean graphical)
   {
      this.graphical = graphical;
   }

   @Override
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(CommandExecutionListener listener)
   {
      return null;
   }

   @Override
   public boolean isGUI()
   {
      return graphical;
   }

}
