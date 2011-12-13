/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.project.dependencies;

import org.jboss.forge.project.Project;

/**
 * Responsible for installing a given {@link Dependency} into the current project. Resolves available dependencies.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface DependencyInstaller
{
   /**
    * Install given {@link Dependency} with the default {@link ScopeType}. This method overwrites existing dependencies.
    */
   Dependency install(Project project, Dependency dependency);

   /**
    * Install given {@link Dependency} with the given {@link ScopeType}. This method overwrites existing dependencies.
    */
   Dependency install(Project project, Dependency dependency, ScopeType type);

   /**
    * Returns whether or not the given {@link Dependency} is installed.
    */
   boolean isInstalled(Project project, Dependency dependency);
}
