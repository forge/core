/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.util.Assert;

/**
 * An abstract representation of a template. Concrete instances of this class are used to wrap {@link Resource}
 * instances representing template resources.
 * 
 * @author Vineet Reynolds
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class AbstractTemplate implements Template
{

   final Resource<?> resource;

   protected AbstractTemplate(Resource<?> resource)
   {
      Assert.notNull(resource, "The provided resource cannot be null.");
      this.resource = resource;
   }

   /**
    * Fetches the underlying resource associated with this Template instance.
    * 
    * @return the resource associated with this instance
    */
   @Override
   public Resource<?> getResource()
   {
      return resource;
   }
}
