/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.resource;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.resource.events.ResourceEvent;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Exported
public interface ResourceFactory
{
   /**
    * Create a {@link Resource} of the given type, using the provided underlying resource instance.
    */
   public abstract <E, T extends Resource<E>> T create(Class<T> type, E underlyingResource);

   /**
    * Create a {@link Resource} to represent the provided underlying resource. The resource type will be detected
    * automatically.
    */
   public abstract <E> Resource<E> create(E underlyingResource);

   /**
    * Broadcast a {@link ResourceEvent}
    */
   public abstract ResourceFactory fireEvent(ResourceEvent event);

}