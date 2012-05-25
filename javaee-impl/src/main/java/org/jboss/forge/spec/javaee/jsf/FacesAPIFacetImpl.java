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
package org.jboss.forge.spec.javaee.jsf;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.FacesAPIFacet;
import org.jboss.forge.spec.javaee.ServletFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.jsf.api")
@RequiresFacet(ServletFacet.class)
public class FacesAPIFacetImpl extends FacesFacetImpl implements FacesAPIFacet
{
   public static final Dependency JAVAEE6_FACES = DependencyBuilder
            .create("org.jboss.spec.javax.faces:jboss-jsf-api_2.0_spec").setScopeType(ScopeType.PROVIDED);
   public static final Dependency JAVAEE6_FACES_21 = DependencyBuilder
            .create("org.jboss.spec.javax.faces:jboss-jsf-api_2.1_spec").setScopeType(ScopeType.PROVIDED);

   @Inject
   public FacesAPIFacetImpl(final DependencyInstaller installer, final ShellPrintWriter out)
   {
       super(installer, out);
   }

   @Override
   public boolean isInstalled()
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      return deps.hasEffectiveDependency(JAVAEE6_FACES) || deps.hasEffectiveDependency(JAVAEE6_FACES_21);
   }

   @Override
   public boolean install()
   {
      super.install();

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      if (!deps.hasDirectManagedDependency(JAVAEE6))
      {
         getInstaller().installManaged(project, JAVAEE6);
      }
      if(deps.hasEffectiveManagedDependency(JAVAEE6_FACES) && !deps.hasEffectiveDependency(JAVAEE6_FACES))
      {
         getInstaller().install(project, JAVAEE6_FACES);
      }
      else if(deps.hasEffectiveManagedDependency(JAVAEE6_FACES_21) && !deps.hasEffectiveDependency(JAVAEE6_FACES_21))
      {
         getInstaller().install(project, JAVAEE6_FACES_21);
      }

      return isInstalled();
   }

}
