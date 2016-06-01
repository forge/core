/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class TemplateFactoryImpl implements TemplateFactory
{
   private Imported<TemplateGenerator> generators;

   @Override
   public Template create(Resource<?> template, Class<? extends Template> type)
   {
      Assert.notNull(template, "Template resource cannot be null");
      Assert.isTrue(template.exists(), "Template does not exist: " + template);

      for (TemplateGenerator generator : getTemplateGenerators())
      {
         if (generator.handles(type))
         {
            return generator.create(template, type);
         }
      }

      return null;
   }

   private Imported<TemplateGenerator> getTemplateGenerators()
   {
      if (generators == null)
      {
         AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
         generators = addonRegistry.getServices(TemplateGenerator.class);
      }
      return generators;
   }
}
