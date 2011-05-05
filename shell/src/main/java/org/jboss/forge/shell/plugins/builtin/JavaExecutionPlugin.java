/*
 * JBoss, by Red Hat.
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

package org.jboss.forge.shell.plugins.builtin;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaExecutionFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;

@Alias("execute-java")
@Topic("Project")
@Help("Execute a main method on a project class")
public class JavaExecutionPlugin implements Plugin
{
   @Inject Project project;

   @DefaultCommand
   public void executeJavaClass(@Option(name = "class", type = PromptType.JAVA_CLASS, required = true) JavaResource classToExecute,
                                @Option(name = "arguments") String arguments) throws Exception
   {
      String qualifiedName = classToExecute.getJavaSource().getQualifiedName();
      JavaExecutionFacet facet = project.getFacet(JavaExecutionFacet.class);
      if (arguments != null)
      {
         facet.executeProjectClass(qualifiedName, arguments.split(" "));
      } else
      {
         facet.executeProjectClass(qualifiedName);
      }

   }
}
