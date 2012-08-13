/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.scaffold.plugins;

import java.util.List;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.TemplateStrategy;
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
   public List<Resource<?>> setup(String targetDir, Resource<?> template, boolean overwrite)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Resource<?>> generateTemplates(String targetDir, boolean overwrite)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Resource<?>> generateIndex(String targetDir, Resource<?> template, boolean overwrite)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Resource<?>> generateFromEntity(String targetDir, Resource<?> template, JavaClass entity,
            boolean overwrite)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Resource<?>> getGeneratedResources(String targetDir)
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
