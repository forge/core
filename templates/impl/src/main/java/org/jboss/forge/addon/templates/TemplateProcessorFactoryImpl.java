/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.templates;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class TemplateProcessorFactoryImpl implements TemplateProcessorFactory
{
   @Inject
   private Imported<TemplateGenerator> generators;

   @Override
   public TemplateProcessor createProcessorFor(Resource<?> resource)
   {
      Assert.notNull(resource, "Template resource cannot be null");
      Assert.isTrue(resource.exists(), "Template does not exist: " + resource);
      for (TemplateGenerator generator : generators)
      {
         if (generator.handles(resource))
         {
            return new TemplateProcessorImpl(generator, resource);
         }
      }
      throw new IllegalStateException("No generator found for [" + resource + "]");
   }
}
