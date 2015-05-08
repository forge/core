/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.furnace.versions.Version;

/**
 * {@link Facet} types that implement this interface are individual components of the Java Enterprise Edition
 * Specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface JavaEEFacet extends ProjectFacet
{
   String DEFAULT_SERVICE_PACKAGE = "service";
   String DEFAULT_VIEW_PACKAGE = "view";
   String DEFAULT_CONVERTER_PACKAGE = "converter";
   String DEFAULT_VALIDATOR_PACKAGE = "validator";
   String DEFAULT_ENTITY_PACKAGE = "model";
   String DEFAULT_CONSTRAINT_PACKAGE = "constraints";
   String DEFAULT_CDI_PACKAGE = "beans";
   String DEFAULT_CDI_EXTENSIONS_PACKAGE = "beans.extensions";

   /**
    * Return the {@link Version} of the specification for which this facet represents.
    */
   public Version getSpecVersion();
}
