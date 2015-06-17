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
   /**
    * @deprecated Use {@link JavaEEPackageConstants#DEFAULT_ENTITY_PACKAGE} instead
    */
   @Deprecated
   public static final String DEFAULT_ENTITY_PACKAGE = JavaEEPackageConstants.DEFAULT_ENTITY_PACKAGE;
   /**
    * @deprecated Use {@link JavaEEPackageConstants#DEFAULT_CONSTRAINT_PACKAGE} instead
    */
   @Deprecated
   public static final String DEFAULT_CONSTRAINT_PACKAGE = JavaEEPackageConstants.DEFAULT_CONSTRAINT_PACKAGE;
   /**
    * @deprecated Use {@link JavaEEPackageConstants#DEFAULT_CDI_PACKAGE} instead
    */
   @Deprecated
   public static final String DEFAULT_CDI_PACKAGE = JavaEEPackageConstants.DEFAULT_CDI_PACKAGE;

   /**
    * Return the {@link Version} of the specification for which this facet represents.
    */
   Version getSpecVersion();

   /**
    * Return the specification name for which this facet represents.
    */
   String getSpecName();
}