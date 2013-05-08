/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.jboss.forge.shell.util.OSUtils;

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
      File pluginDirFile;

      String pluginDir = System.getProperty(Bootstrap.PROP_PLUGIN_DIR);
      if (pluginDir == null)
      {
         pluginDirFile = new File(getProperty(ShellImpl.PROP_FORGE_CONFIG_DIR).toString(), "plugins");
      }
      else
      {
         pluginDirFile = new File(pluginDir);
      }
      FileResource<?> resource = (FileResource<?>) resourceFactory.getResourceFrom(pluginDirFile);
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
      String forgeConfigDir = (String) getProperty(ShellImpl.PROP_FORGE_CONFIG_DIR);
      if (forgeConfigDir == null)
      {
         forgeConfigDir = System.getProperty(ShellImpl.PROP_FORGE_CONFIG_DIR, OSUtils.getDefaultForgePath());
      }
      FileResource<?> resource = (FileResource<?>) resourceFactory.getResourceFrom(new File(forgeConfigDir));
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

   @Override
   public DirectoryResource getForgeHome()
   {
      String forgeHome = System.getProperty(ShellImpl.FORGE_HOME_SYSTEM_PROPERTY);
      return resourceFactory.getResourceFrom(new File(forgeHome)).reify(DirectoryResource.class);
   }

   @Override
   public boolean isEmbedded()
   {
      return Boolean.getBoolean("forge.compatibility.IDE");
   }
}
