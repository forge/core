/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyRepository;

public class ProfileImpl implements Profile
{
   private String id;
   private boolean activateByDefault;
   private List<Dependency> dependencies = new ArrayList<Dependency>();
   private List<DependencyRepository> repositories = new ArrayList<DependencyRepository>();
   private Properties properties = new Properties();

   @Override public String getId()
   {
      return id;
   }

   @Override public boolean isActiveByDefault()
   {
      return activateByDefault;
   }

   @Override public List<Dependency> listDependencies()
   {
      return dependencies;
   }

   @Override public List<DependencyRepository> listRepositories()
   {
      return repositories;
   }

   @Override public Properties getProperties()
   {
      return properties;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public void setActivateByDefault(boolean activateByDefault)
   {
      this.activateByDefault = activateByDefault;
   }
}
