/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import org.jboss.forge.resources.Resource;

/**
 * Signal the Shell to pick up the given resource.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PickupResource
{
   private final Resource<?> resource;

   public PickupResource(final Resource<?> resource)
   {
      this.resource = resource;
   }

   public Resource<?> getResource()
   {
      return resource;
   }
}
