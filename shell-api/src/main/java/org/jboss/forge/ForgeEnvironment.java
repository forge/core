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
package org.jboss.forge;

import java.util.Map;

import org.jboss.forge.resources.DirectoryResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ForgeEnvironment
{

   /**
    * Return the directory Forge is using to store and load third-party plugins.
    */
   DirectoryResource getPluginDirectory();

   /**
    * Return true if Forge is currently operating with the assumption that an Internet connection is available.
    */
   boolean isOnline();

   /**
    * Set a configuration property for the current Forge execution.
    */
   void setProperty(String name, Object value);

   /**
    * Get a map of all configuration properties for the current Forge execution.
    */
   Map<String, Object> getProperties();

   /**
    * Get a named property for the current Forge execution
    */
   Object getProperty(String name);

   /**
    * Get a named property for the current Forge execution
    */
   void removeProperty(String funcName);
}
