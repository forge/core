/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.monitor;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * Monitors a {@link Resource} for changes
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ResourceMonitor
{
   /**
    * The {@link Resource} this Monitor is bound to.
    */
   public Resource<?> getResource();

   /**
    * Register a listener for resource events.
    */
   ListenerRegistration<ResourceListener> addResourceListener(ResourceListener listener);

   /**
    * Cancels this monitor. All registered listeners are automatically discarded.
    */
   public void cancel();
}
