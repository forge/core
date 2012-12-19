/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource.events;

import org.jboss.forge.resource.Resource;

/**
 * Fired when a temporary {@link Resource} has been created. Used to distinguish between
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class TempResourceCreated extends ResourceCreated
{
   public TempResourceCreated(final Resource<?> resource)
   {
      super(resource);
   }
}
