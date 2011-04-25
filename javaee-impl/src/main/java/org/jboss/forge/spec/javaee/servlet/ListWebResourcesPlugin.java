/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.spec.javaee.servlet;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.spec.javaee.ServletFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("list-web-resources")
@Topic("File & Resources")
@RequiresProject
@RequiresFacet(ServletFacet.class)
@Help("Lists all project Web Resources")
public class ListWebResourcesPlugin implements Plugin
{
   private final Project project;
   private final Shell shell;

   @Inject
   public ListWebResourcesPlugin(final Project project, final Shell shell)
   {
      this.project = project;
      this.shell = shell;
   }

   @DefaultCommand
   public void list(@Option(required = false) String filter)
   {
      ServletFacet web = project.getFacet(ServletFacet.class);

      List<Resource<?>> resources = web.getResources();
      for (Resource<?> file : resources)
      {
         shell.println(file.getFullyQualifiedName());
      }
   }
}
