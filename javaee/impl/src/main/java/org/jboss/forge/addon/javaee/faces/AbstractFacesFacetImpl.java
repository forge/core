/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.faces.FacesFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 * Common Implementation for all JSF versions
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractFacesFacetImpl extends AbstractJavaEEFacet implements FacesFacet
{
   public AbstractFacesFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

}
