/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee;

import java.io.IOException;

import javax.inject.Inject;
import javax.persistence.GenerationType;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.ejb.EJBFacet_3_2;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.JPAFacet_2_0;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.parser.java.projects.JavaProjectType;
import org.jboss.forge.addon.parser.java.projects.JavaWebProjectType;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;

/**
 * Helps with the configuration of a project
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectHelper
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private JavaWebProjectType javaWebProjectType;

   @Inject
   private JavaProjectType javaProjectType;

   @Inject
   private PersistenceOperations persistenceOperations;

   /**
    * Creates a project installing the required facets from {@link JavaWebProjectType#getRequiredFacets()}
    */
   public Project createWebProject()
   {
      return projectFactory.createTempProject(javaWebProjectType.getRequiredFacets());
   }

   /**
    * Creates a project installing the required facets from {@link JavaProjectType#getRequiredFacets()}
    */
   public Project createJavaLibraryProject()
   {
      return projectFactory.createTempProject(javaProjectType.getRequiredFacets());
   }

   /**
    * Installs the {@link EJBFacet_3_2} facet
    */
   public EJBFacet_3_2 installEJB_3_2(Project project)
   {
      return facetFactory.install(project, EJBFacet_3_2.class);
   }

   /**
    * Installs the {@link JPAFacet} facet
    */
   public JPAFacet_2_0 installJPA_2_0(Project project)
   {
      return facetFactory.install(project, JPAFacet_2_0.class);
   }

   public JavaResource createJPAEntity(Project project, String entityName) throws IOException
   {
      String packageName = project.getFacet(MetadataFacet.class).getTopLevelPackage() + ".model";
      return persistenceOperations.newEntity(project, entityName, packageName, GenerationType.AUTO);
   }
}