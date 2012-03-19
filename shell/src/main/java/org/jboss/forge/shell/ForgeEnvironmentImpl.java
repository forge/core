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
package org.jboss.forge.shell;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@ApplicationScoped
public class ForgeEnvironmentImpl implements ForgeEnvironment
{

   private final Map<String, Object> properties = new HashMap<String, Object>();

   @Inject
   private ResourceFactory resourceFactory;

   @Override
   public String getRuntimeVersion()
   {
      String version = (String) getProperty(ShellImpl.PROP_FORGE_VERSION);
      if (version == null)
      {
         version = getClass().getPackage().getImplementationVersion();
      }
      return version;
   }

   @Override
   public DirectoryResource getPluginDirectory()
   {
      String pluginDir = System.getProperty(Bootstrap.PROP_PLUGIN_DIR);
      if (pluginDir == null) {
         pluginDir = getProperty(ShellImpl.PROP_FORGE_CONFIG_DIR) + "plugins/";
      }
      FileResource<?> resource = (FileResource<?>) resourceFactory.getResourceFrom(new File(pluginDir));
      if (!resource.exists())
      {
         resource.mkdirs();
      }
      return resource.reify(DirectoryResource.class);
   }

   @Override
   public boolean isOnline()
   {
      Object offline = getProperty(ShellImpl.OFFLINE_FLAG);
      return offline == null ? true : !Boolean.parseBoolean(offline.toString());
   }

   @Override
   public void setProperty(final String name, final Object value)
   {
      properties.put(name, value);
   }

   @Override
   public Object getProperty(final String name)
   {
      return properties.get(name);
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return properties;
   }

   @Override
   public void removeProperty(final String name)
   {
      properties.remove(name);
   }

   @Override
   public DirectoryResource getConfigDirectory()
   {
      FileResource<?> resource = (FileResource<?>) resourceFactory.getResourceFrom(new File(
               (String) getProperty(ShellImpl.PROP_FORGE_CONFIG_DIR)));
      if (!resource.exists())
      {
         if (!resource.mkdirs())
         {
            System.err.println("could not create config directory: " + resource.getFullyQualifiedName());
         }
      }
      return resource.reify(DirectoryResource.class);
   }

   @Override
   public FileResource<?> getUserConfiguration()
   {
      return getConfigDirectory().getChild("config.xml").reify(FileResource.class);
   }
}
