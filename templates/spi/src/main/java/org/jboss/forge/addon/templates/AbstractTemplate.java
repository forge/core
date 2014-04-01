package org.jboss.forge.addon.templates;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.util.Assert;

/**
 * An abstract representation of a template. Concrete instances of this class are used to wrap {@link Resource}
 * instances representing template resources.
 *
 * @author Vineet Reynolds
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
   public Resource<?> getResource()
   {
      return resource;
   }

   /**
    * Indicates whether the template exists or not, usually through it's underlying resource.
    *
    * @return whether the template exists or not
    */
   public boolean exists()
   {
      return resource.exists();
   }
}
