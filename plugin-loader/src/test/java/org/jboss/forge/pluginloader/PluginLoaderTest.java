/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.pluginloader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.jboss.modules.AssertionSetting;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ModuleSpec.Builder;
import org.jboss.modules.ResourceLoader;
import org.jboss.modules.ResourceLoaderSpec;
import org.jboss.modules.ResourceLoaders;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PluginLoaderTest
{
   @Test
   public void testLoadPlugin() throws Exception
   {
			  // for now, ignore this test
			  if(1+1==2)
						 return;


      ModuleLoader loader = new ModuleLoader()
      {
         @Override
         public String toString()
         {
            return this.getClass().getName();
         }

         @Override
         protected ModuleSpec findModule(final ModuleIdentifier module) throws ModuleLoadException
         {
            ModuleIdentifier forge = ModuleIdentifier.create("org.jboss.forge:shell");
            if (forge.equals(module))
            {
               Builder builder = ModuleSpec.build(forge);
               builder.setMainClass("org.jboss.forge.shell.Bootstrap");
               builder.setAssertionSetting(AssertionSetting.DISABLED);
               try
               {
                  JarFile jarFile = new JarFile(
                           new File(
                                    "/Users/lbaxter/.m2/repository/org/jboss/forge/forge-shell/1.0.0-SNAPSHOT/forge-shell-1.0.0-SNAPSHOT.jar"));
                  ResourceLoader jarResourceLoader = ResourceLoaders
                           .createJarResourceLoader("foo", jarFile);
                  ResourceLoaderSpec resourceLoaderSpec = ResourceLoaderSpec
                           .createResourceLoaderSpec(jarResourceLoader);
                  builder.addResourceRoot(resourceLoaderSpec);
               }
               catch (IOException e)
               {
                  throw new RuntimeException(e);
               }

               return builder.create();
            }
            else
               throw new RuntimeException("Unsupported Module: " + module);
         }
      };

      Module shell = loader.loadModule(ModuleIdentifier.create("org.jboss.forge:shell"));

      assertFalse(MockMain.ran);
      shell.run(new String[] {});
      assertTrue(MockMain.ran);
   }
}
