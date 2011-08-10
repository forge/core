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
package org.metawidget.forge;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.seam.render.TemplateCompiler;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("metawidget")
@RequiresFacet({ WebResourceFacet.class,
         DependencyFacet.class,
         PersistenceFacet.class,
         CDIFacet.class,
         FacesFacet.class })
public class MetawidgetScaffold extends MetawidgetScaffoldBase
{
   @Inject
   public MetawidgetScaffold(final ShellPrompt prompt, final ShellPrintWriter writer,
            final TemplateCompiler compiler,
            final Event<InstallFacets> install)
   {
      super(prompt, writer, compiler, install);
   }

   @Override
   protected List<Dependency> getMetawidgetDependencies()
   {
      return Arrays.asList(
               (Dependency) DependencyBuilder.create("org.metawidget.modules.faces:metawidget-faces"),
               (Dependency) DependencyBuilder.create("org.metawidget.modules:metawidget-annotation"),
               (Dependency) DependencyBuilder.create("org.metawidget.modules:metawidget-java5"),
               (Dependency) DependencyBuilder.create("org.metawidget.modules:metawidget-jpa"),
               (Dependency) DependencyBuilder.create("org.metawidget.modules:metawidget-beanvalidation"));
   }
}
