/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.templates.AbstractTemplate;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * An abstract representation of a Freemarker template. Consumers of this class create instances of it with
 * {@link Resource} instances to wrap Freemarker template resources. This class is used to distinguish Freemarker
 * templates from other templates.
 * 
 * @author Vineet Reynolds
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class FreemarkerTemplateImpl extends AbstractTemplate implements FreemarkerTemplate
{
   private final freemarker.template.Configuration config;
   private final ResourceTemplateLoader loader;

   public FreemarkerTemplateImpl(ResourceTemplateLoader loader, Resource<?> resource, Configuration config)
   {
      super(resource);
      this.loader = loader;

      this.config = config;
   }

   @Override
   public String process(Object model) throws IOException
   {
      StringWriter writer = new StringWriter();
      process(model, writer);
      return writer.toString();
   }

   @Override
   public void process(Object model, Writer output) throws IOException
   {
      String id = loader.register(this.getResource());
      try
      {
         freemarker.template.Template templateFile = getFreemarkerConfig().getTemplate(id);
         templateFile.process(model, output);
         output.flush();
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
   public freemarker.template.Configuration getFreemarkerConfig()
   {
      return config;
   }
}
