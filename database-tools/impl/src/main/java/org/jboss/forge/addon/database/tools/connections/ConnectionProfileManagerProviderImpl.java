/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections;

import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

public class ConnectionProfileManagerProviderImpl implements ConnectionProfileManagerProvider
{
   private ConnectionProfileManager connectionProfileManager;

   @Override
   public void setConnectionProfileManager(ConnectionProfileManager manager)
   {
      this.connectionProfileManager = manager;
   }

   @Override
   public ConnectionProfileManager getConnectionProfileManager()
   {
      if (connectionProfileManager != null)
      {
         return connectionProfileManager;
      }
      else
      {
         return SimpleContainer.getServices(getClass().getClassLoader(), ConnectionProfileManager.class).get();
      }
   }

}
