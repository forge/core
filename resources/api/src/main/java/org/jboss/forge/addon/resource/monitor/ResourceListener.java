/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.monitor;

import org.jboss.forge.addon.resource.events.ResourceEvent;

/**
 * A Listener for Resource events
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ResourceListener
{
   void processEvent(ResourceEvent event);
}
