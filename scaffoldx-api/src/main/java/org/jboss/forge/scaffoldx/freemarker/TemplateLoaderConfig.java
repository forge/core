/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.freemarker;

import java.io.File;

/**
 * A configuration wrapper for the Freemarker File and Classpath Template loaders.
 * 
 * @author Vineet Reynolds
 */
public class TemplateLoaderConfig
{

   private Class<?> loaderClass;
   private String basePath;
   private File templateBaseDir;

   /**
    * For CDI. Do not invoke this directly
    */
   public TemplateLoaderConfig()
   {
      // Do nothing!
   }
   
   public TemplateLoaderConfig(File templateBaseDir, Class<?> loaderClass, String basePath)
   {
      initFields(templateBaseDir, loaderClass, basePath);
   }

   private void initFields(File templateBaseDir, Class<?> loaderClass, String basePath)
   {
      if (loaderClass == null)
      {
         throw new IllegalArgumentException("The loader class cannot be null.");
      }
      if (basePath == null)
      {
         throw new IllegalArgumentException("The template base path cannot be null");
      }
      this.templateBaseDir = templateBaseDir;
      this.loaderClass = loaderClass;
      this.basePath = basePath;
   }

   public Class<?> getLoaderClass()
   {
      return loaderClass;
   }

   public String getBasePath()
   {
      return basePath;
   }

   public File getTemplateBaseDir()
   {
      return templateBaseDir;
   }

}
