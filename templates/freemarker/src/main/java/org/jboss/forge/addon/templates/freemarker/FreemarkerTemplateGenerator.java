/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.templates.freemarker;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.templates.Template;
import org.jboss.forge.addon.templates.TemplateGenerator;

import freemarker.template.Configuration;

/**
 * A Freemarker implementation of a {@link TemplateGenerator}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class FreemarkerTemplateGenerator implements TemplateGenerator
{
   @Inject
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
      return new FreemarkerTemplateImpl(loader, template, getConfiguration());
   }

   private Configuration getConfiguration()
   {
      if (config == null)
      {
         config = new Configuration();
         config.setTemplateLoader(loader);
      }
      return config;
   }

}
