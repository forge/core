/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces;

import java.util.List;

import org.jboss.forge.addon.javaee.faces.FacesFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.scaffold.spi.AccessStrategy;

/**
 * Faces Scaffold Access Strategy
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacesAccessStrategy implements AccessStrategy
{
   final FacesFacet<?> faces;

   public FacesAccessStrategy(final Project project)
   {
      this.faces = project.getFacet(FacesFacet.class);
   }

   @Override
   public List<String> getWebPaths(final Resource<?> resource)
   {
      return this.faces.getWebPaths(resource);
   }

   @Override
   public Resource<?> fromWebPath(final String path)
   {
      return this.faces.getResourceForWebPath(path);
   }

}
