/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates.freemarker;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.templates.Template;
import org.jboss.forge.addon.templates.TemplateGenerator;

import freemarker.template.Configuration;

/**
 * A Freemarker implementation of a {@link TemplateGenerator}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FreemarkerTemplateGenerator implements TemplateGenerator
{
   private ResourceTemplateLoader loader;
   private Configuration config;

   @Override
   public boolean handles(Class<? extends Template> type)
   {
      return FreemarkerTemplate.class.isAssignableFrom(type);
   }

   @Override
   public Template create(Resource<?> template, Class<? extends Template> type)
   {
      return new FreemarkerTemplateImpl(getResourceTemplateLoader(), template, getConfiguration());
   }

   private Configuration getConfiguration()
   {
      if (config == null)
      {
         config = new Configuration();
         config.setTemplateLoader(getResourceTemplateLoader());
      }
      return config;
   }

   private ResourceTemplateLoader getResourceTemplateLoader()
   {
      if (loader == null)
      {
         loader = new ResourceTemplateLoader();
      }
      return loader;
   }

}
