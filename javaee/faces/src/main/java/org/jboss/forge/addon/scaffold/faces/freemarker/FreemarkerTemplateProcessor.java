/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A template processor that processes specified Freemarker files to generate text output.
 * 
 */
public class FreemarkerTemplateProcessor
{

   private freemarker.template.Configuration freemarkerConfig;

   public FreemarkerTemplateProcessor()
   {
      freemarkerConfig = new freemarker.template.Configuration();
      freemarkerConfig.setClassForTemplateLoading(getClass(), "/");
      freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
   }
   
   /**
    * Provides a {@link Template} representation of a Freemarker template present at a specified location.
    * 
    * @param templateLocation The location of the template relative to the classpath
    * @return The Freemarker {@link Template} instance for the specified location
    */
   public Template getTemplate(String templateLocation)
   {
      try
      {
         Template templateFile = freemarkerConfig.getTemplate(templateLocation);
         return templateFile;
      }
      catch (IOException ioEx)
      {
         throw new RuntimeException(ioEx);
      }
   }

   /**
    * Processes the provided data model with the specified Freemarker template
    * 
    * @param map the data model to use for template processing.
    * @param templateLocation The location of the template relative to the classpath
    * @return The text output after successfully processing the template
    */
   public String processTemplate(Map<Object, Object> map, String templateLocation)
   {
      Writer output = new StringWriter();
      try
      {
         Template templateFile = freemarkerConfig.getTemplate(templateLocation);
         templateFile.process(map, output);
         output.flush();
      }
      catch (IOException ioEx)
      {
         throw new RuntimeException(ioEx);
      }
      catch (TemplateException templateEx)
      {
         throw new RuntimeException(templateEx);
      }
      return output.toString();
   }
   
   /**
    * Processes the provided data model with the specified Freemarker template
    * 
    * @param map the data model to use for template processing.
    * @param template The Freemarker {@link Template} to be processed.
    * @return The text output after successfully processing the template
    */
   public String processTemplate(Map<Object, Object> map, Template template)
   {
      Writer output = new StringWriter();
      try
      {
         template.process(map, output);
         output.flush();
      }
      catch (IOException ioEx)
      {
         throw new RuntimeException(ioEx);
      }
      catch (TemplateException templateEx)
      {
         throw new RuntimeException(templateEx);
      }
      return output.toString();
   }

}
