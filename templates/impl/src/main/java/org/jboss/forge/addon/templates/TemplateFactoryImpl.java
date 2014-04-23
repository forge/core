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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class TemplateFactoryImpl implements TemplateFactory
{
   @Inject
   private Imported<TemplateGenerator> generators;

   @Override
   public Template create(Resource<?> template, Class<? extends Template> type)
   {
      Assert.notNull(template, "Template resource cannot be null");
      Assert.isTrue(template.exists(), "Template does not exist: " + template);

      for (TemplateGenerator generator : generators)
      {
         if (generator.handles(type))
         {
            return generator.create(template, type);
         }
      }

      return null;
   }
}
