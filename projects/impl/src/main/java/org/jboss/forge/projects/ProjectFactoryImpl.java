/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.projects;

import org.jboss.forge.container.util.Predicate;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ProjectFactoryImpl implements ProjectFactory
{
   @Override
   public Project findProject(Resource<?> target)
   {
      return null;
   }

   @Override
   public Project findProject(Resource<?> target, Predicate<Project> filter)
   {
      return null;
   }

   @Override
   public Project createProject(DirectoryResource targetDir, ProjectType value)
   {
      return null;
   }

}
