/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.templates;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.jboss.forge.addon.resource.Resource;

/**
 * {@link TemplateProcessor} implementation
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TemplateProcessorImpl implements TemplateProcessor
{
   private final TemplateGenerator generator;
   private final Resource<?> resource;

   TemplateProcessorImpl(TemplateGenerator generator, Resource<?> resource)
   {
      super();
      this.generator = generator;
      this.resource = resource;
   }

   @Override
   public String process(Map<?, ?> map) throws IOException
   {
      StringWriter writer = new StringWriter();
      process(map, writer);
      return writer.toString();
   }

   @Override
   @SuppressWarnings("unchecked")
   public void process(Map<?, ?> map, Writer output) throws IOException
   {
      generator.process((Map<Object, Object>) map, resource, output);
   }
}