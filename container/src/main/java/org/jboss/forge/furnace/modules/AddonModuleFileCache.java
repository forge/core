/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.modules;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Sets;
import org.jboss.modules.ModuleIdentifier;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class AddonModuleJarFileCache
{
   private static final Logger logger = Logger.getLogger(AddonModuleJarFileCache.class.getName());
   private Map<ModuleIdentifier, Set<JarFile>> map = new ConcurrentHashMap<ModuleIdentifier, Set<JarFile>>();

   public void closeJarFileReferences(ModuleIdentifier id)
   {
      Assert.notNull(id, "Module reference must not be null.");

      Set<JarFile> files = map.remove(id);
      if (files != null)
      {
         for (JarFile file : files)
         {
            try
            {
               logger.log(Level.FINE, "Closing JarFile [" + file.getName() + "]");
               file.close();
            }
            catch (IOException e)
            {
               logger.log(Level.WARNING, "Could not close JAR file reference [" + file + "] for module [" + id + "]", e);
            }
         }
      }
   }

   public void addJarFileReference(ModuleIdentifier id, JarFile file)
   {
      Assert.notNull(id, "Module reference must not be null.");
      Assert.notNull(file, "JarFile reference must not be null.");

      logger.log(Level.FINE, "Adding JarFile [" + file.getName() + "] for module [" + id + "]");
      Set<JarFile> files = map.get(id);
      if (files == null)
      {
         files = Sets.getConcurrentSet();
         map.put(id, files);
      }

      files.add(file);
   }

}
