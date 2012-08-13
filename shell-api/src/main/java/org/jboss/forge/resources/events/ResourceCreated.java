/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources.events;

import org.jboss.forge.QueuedEvent;
import org.jboss.forge.resources.Resource;

/**
 * Fired when a {@link Resource} has been created.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@QueuedEvent
public class ResourceCreated extends ResourceEvent
{

   public ResourceCreated(final Resource<?> resource)
   {
      super(resource);
   }
}
