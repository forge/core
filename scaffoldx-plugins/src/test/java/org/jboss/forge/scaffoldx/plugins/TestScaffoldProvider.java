/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.scaffoldx.plugins;

import java.util.List;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffoldx.AccessStrategy;
import org.jboss.forge.scaffoldx.ScaffoldProvider;
import org.jboss.forge.scaffoldx.TemplateStrategy;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Help;

@Alias("test")
@Help("Foo")
public class TestScaffoldProvider implements ScaffoldProvider
{

   @Override
   public Project getProject()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setProject(Project project)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public boolean install()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isInstalled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean uninstall()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public List<Resource<?>> setup(String targetDir, boolean overwrite, boolean installTemplates)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Resource<?>> generateFrom(List<Resource<?>> resource, String targetDir, boolean overwrite)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public AccessStrategy getAccessStrategy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public TemplateStrategy getTemplateStrategy()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
