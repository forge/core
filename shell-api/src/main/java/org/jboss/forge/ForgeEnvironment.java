/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge;

import java.util.Map;

import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ForgeEnvironment
{
   /**
    * Return the current Forge version as a String. E.g.: "1.0.0.Final"
    */
   String getRuntimeVersion();

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

   /**
    * Get the configuration directory.
    */
   DirectoryResource getConfigDirectory();

   /**
    * Get the current User's configuration file.
    */
   FileResource<?> getUserConfiguration();
}
