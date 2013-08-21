/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import javax.enterprise.inject.Produces;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.project.ProjectScoped;

/**
 * A utlity class containing CDI producers for Forge project facets.
 */
public class FacetProducers
{

   @Produces
   @ProjectScoped
   public JavaSourceFacet produceJavaSourceFacet(Project project)
   {
      return project.getFacet(JavaSourceFacet.class);
   }
}
