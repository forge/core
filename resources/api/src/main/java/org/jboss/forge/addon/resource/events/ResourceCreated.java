/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.events;

import org.jboss.forge.addon.resource.Resource;

/**
 * Fired when a {@link Resource} has been created.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResourceCreated extends AbstractResourceEvent
{
   public ResourceCreated(final Resource<?> resource)
   {
      super(resource);
   }
}
