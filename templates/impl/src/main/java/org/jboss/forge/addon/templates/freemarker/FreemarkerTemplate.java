package org.jboss.forge.addon.templates.freemarker;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.templates.Template;

/**
 * An abstract representation of a Freemarker template. Consumers of this class create instances of it with
 * {@link Resource} instances to wrap Freemarker template resources. This class is used to distinguish Freemarker
 * templates from other templates.
 * 
 * @author Vineet Reynolds
 */
public class FreemarkerTemplate extends Template
{
   public FreemarkerTemplate(Resource<?> resource)
   {
      super(resource);
   }
}
