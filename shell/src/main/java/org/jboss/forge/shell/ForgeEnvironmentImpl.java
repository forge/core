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
   public DirectoryResource getPluginDirectory()
   {
      String pluginPath = getProperty("FORGE_CONFIG_DIR") + "plugins/";
      FileResource<?> resource = (FileResource<?>) resourceFactory.getResourceFrom(new File(pluginPath));
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
   public void removeProperty(String name)
   {
      properties.remove(name);
   }
}
