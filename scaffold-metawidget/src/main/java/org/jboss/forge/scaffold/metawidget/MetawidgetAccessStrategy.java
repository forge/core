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
package org.jboss.forge.scaffold.metawidget;

import java.util.List;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.spec.javaee.FacesFacet;

/**
 * Metawidget Scaffold Access Strategy
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MetawidgetAccessStrategy implements AccessStrategy
{
   final FacesFacet faces;

   public MetawidgetAccessStrategy(final Project project)
   {
      faces = project.getFacet(FacesFacet.class);
   }

   @Override
   public List<String> getWebPaths(final Resource<?> r)
   {
      return faces.getWebPaths(r);
   }

   @Override
   public Resource<?> fromWebPath(final String path)
   {
      return faces.getResourceForWebPath(path);
   }

}
