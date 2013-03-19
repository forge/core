/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.scaffoldx.ScaffoldProvider;
import org.jboss.forge.scaffoldx.ScaffoldQualifier;
import org.jboss.forge.shell.project.ProjectScoped;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A 'client' class for Freemarker, that can be utilized by {@link ScaffoldProvider} implementations. This uses a
 * {@link MultiTemplateLoader} to manage multiple templates sources.
 * 
 * @author Vineet Reynolds 
 */
@ProjectScoped
public class FreemarkerClient
{

   private Configuration config;

   /**
    * For CDI. Do not invoke this directly.
    */
   public FreemarkerClient()
   {
      // Do nothing!
   }

   /**
    * Create a new {@link FreemarkerClient}. The {@link MultiTemplateLoader} is initialized at this point. The
    * {@link MultiTemplateLoader} is configured to load templates from the
    * <code>TemplateLoaderConfig.templateBaseDir</code> attribute if present. The {@link MultiTemplateLoader} is also
    * configured to load templates from the <code>TemplateLoaderConfig.templateBasePath</code> path in the classpath of
    * the <code>TemplateLoaderConfig.loaderClass</code> class if not found in the <code>templateBaseDir</code>.
    * 
    * @param instance The CDI injected {@link TemplateLoaderConfig} instance. This is to be produced by scaffold
    *           providers.
    */
   @Inject
   public FreemarkerClient(@ScaffoldQualifier Instance<TemplateLoaderConfig> instance)
   {
      this(instance.get());
   }

   /**
    * Create a new {@link FreemarkerClient}. The {@link MultiTemplateLoader} is initialized at this point. The
    * {@link MultiTemplateLoader} is configured to load templates from the
    * <code>TemplateLoaderConfig.templateBaseDir</code> attribute if present. The {@link MultiTemplateLoader} is also
    * configured to load templates from the <code>TemplateLoaderConfig.templateBasePath</code> path in the classpath of
    * the <code>TemplateLoaderConfig.loaderClass</code> class if not found in the <code>templateBaseDir</code>.
    * 
    * This constructor is in place to aid in testability.
    * 
    * @param loaderConfig The {@link TemplateLoaderConfig} instance that contains the configuration values to initialize
    *           the {@link MultiTemplateLoader} instance.
    */
   public FreemarkerClient(TemplateLoaderConfig loaderConfig)
   {
      List<TemplateLoader> loaders = new ArrayList<TemplateLoader>();
      File templateBaseDir = loaderConfig.getTemplateBaseDir();
      if (templateBaseDir != null)
      {
         try
         {
            loaders.add(new FileTemplateLoader(templateBaseDir));
         }
         catch (IOException ioEx)
         {
            throw new RuntimeException(ioEx);
         }
      }
      loaders.add(new ClassTemplateLoader(loaderConfig.getLoaderClass(), loaderConfig.getBasePath()));
      config = new Configuration();
      config.setTemplateLoader(new MultiTemplateLoader(loaders.toArray(new TemplateLoader[0])));
      config.setObjectWrapper(new DefaultObjectWrapper());
   }

   /**
    * Processes the provided data model with the specified template to produce some generated textual output.
    * 
    * @param root The root node of the data model to be processed by Freemarker.
    * @param inputPath The location of the Freemarker template.
    * @return The output of processing the template by Freemarker
    */
   public String processFTL(Map<String, Object> root, String inputPath)
   {
      try
      {
         Template templateFile = config.getTemplate(inputPath);
         Writer out = new StringWriter();
         templateFile.process(root, out);
         out.flush();
         return out.toString();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      catch (TemplateException e)
      {
         throw new RuntimeException(e);
      }
   }

}
