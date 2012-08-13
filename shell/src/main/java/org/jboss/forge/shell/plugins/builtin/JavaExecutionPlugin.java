/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaExecutionFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

@Alias("execute-java")
@Topic("Project")
@Help("Execute a main method on a project class")
public class JavaExecutionPlugin implements Plugin
{
   @Inject
   Project project;

   @DefaultCommand
   public void executeJavaClass(
            @Option(name = "class", type = PromptType.JAVA_CLASS, required = true) final JavaResource classToExecute,
            @Option(name = "arguments") final String arguments) throws Exception
   {
      String qualifiedName = classToExecute.getJavaSource().getQualifiedName();
      JavaExecutionFacet facet = project.getFacet(JavaExecutionFacet.class);
      if (arguments != null)
      {
         facet.executeProjectClass(qualifiedName, arguments.split(" "));
      }
      else
      {
         facet.executeProjectClass(qualifiedName);
      }

   }
}
