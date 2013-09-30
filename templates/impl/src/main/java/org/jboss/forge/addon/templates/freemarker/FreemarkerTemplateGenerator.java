/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.templates.freemarker;

import java.io.IOException;
import java.io.Writer;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.templates.TemplateGenerator;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A Freemarker implementation of a {@link TemplateGenerator}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class FreemarkerTemplateGenerator implements TemplateGenerator
{
   private freemarker.template.Configuration freemarkerConfig;

   @Inject
   private ResourceTemplateLoader loader;

   @Override
   public void process(Object dataModel, Resource<?> resource, Writer writer) throws IOException
   {
      String id = loader.register(resource);
      try
      {
         Template templateFile = getFreemarkerConfig().getTemplate(id);
         templateFile.process(dataModel, writer);
         writer.flush();
      }
      catch (TemplateException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         loader.dispose(id);
      }
   }

   @Override
   public boolean handles(Resource<?> template)
   {
      return true;
   }

   public freemarker.template.Configuration getFreemarkerConfig()
   {
      if (freemarkerConfig == null)
      {
         freemarkerConfig = new freemarker.template.Configuration();
         freemarkerConfig.setTemplateLoader(loader);
      }
      return freemarkerConfig;
   }
}
