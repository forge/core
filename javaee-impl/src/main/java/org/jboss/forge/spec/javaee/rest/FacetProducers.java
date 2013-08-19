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
