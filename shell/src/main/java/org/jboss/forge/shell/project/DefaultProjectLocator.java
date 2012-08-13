/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.locator.ProjectLocator;
import org.jboss.forge.resources.DirectoryResource;

import com.google.inject.Inject;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DefaultProjectLocator implements ProjectLocator
{
   @Inject
   public DefaultProjectLocator()
   {
   }

   @Override
   public Project createProject(final DirectoryResource dir)
   {
      return null;
   }

   @Override
   public boolean containsProject(final DirectoryResource dir)
   {
      return false;
   }
}
