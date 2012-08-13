/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
