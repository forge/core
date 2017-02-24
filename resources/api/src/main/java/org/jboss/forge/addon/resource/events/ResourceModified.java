/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.events;

import org.jboss.forge.addon.resource.Resource;

/**
 * Fired when a {@link Resource} has been modified.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResourceModified extends AbstractResourceEvent
{
   public ResourceModified(final Resource<?> resource)
   {
      super(resource);
   }
}
