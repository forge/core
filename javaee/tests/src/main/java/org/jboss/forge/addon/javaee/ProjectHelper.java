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
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_0;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.javaee.ejb.EJBFacet_3_2;
import org.jboss.forge.addon.javaee.faces.FacesFacet_2_2;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.JPAFacet_2_0;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.javaee.rest.RestFacet_2_0;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategy;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.javaee.validation.ValidationFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.projects.JavaProjectType;
import org.jboss.forge.addon.parser.java.projects.JavaWebProjectType;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaEnumSource;

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
    * Installs the {@link ServletFacet_3_1} facet
    */
   public ServletFacet_3_1 installServlet_3_1(Project project)
   {
      return facetFactory.install(project, ServletFacet_3_1.class);
   }

   /**
    * Installs the {@link FacesFacet_2_2} facet
    */
   public FacesFacet_2_2 installFaces_2_2(Project project)
   {
      return facetFactory.install(project, FacesFacet_2_2.class);
   }

   /**
    * Installs the {@link JPAFacet} facet
    */
   public JPAFacet_2_0 installJPA_2_0(Project project)
   {
      return facetFactory.install(project, JPAFacet_2_0.class);
   }

   /**
    * Installs the {@link CDIFacet_1_0} facet
    */
   public CDIFacet_1_0 installCDI_1_0(Project project)
   {
      return facetFactory.install(project, CDIFacet_1_0.class);
   }

   /**
    * Installs the {@link CDIFacet_1_1} facet
    */
   public CDIFacet_1_1 installCDI_1_1(Project project)
   {
      return facetFactory.install(project, CDIFacet_1_1.class);
   }

   /**
    * Installs the {@link ValidationFacet} facet
    */
   public ValidationFacet installValidation(Project project)
   {
      return facetFactory.install(project, ValidationFacet.class);
   }

   /**
    * Installs the {@link RestFacet_2_0} facet
    */
   public RestFacet_2_0 installJAXRS_2_0(Project project, RestConfigurationStrategy strategy)
   {
      RestFacet_2_0 facet = facetFactory.install(project, RestFacet_2_0.class);
      facet.setConfigurationStrategy(strategy);
      return facet;
   }

   public JavaResource createJPAEntity(Project project, String entityName) throws IOException
   {
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + ".model";
      return persistenceOperations.newEntity(project, entityName, packageName, GenerationType.AUTO);
   }

   public JavaResource createJPAEmbeddable(Project project, String entityName) throws IOException
   {
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + ".model";
      return persistenceOperations.newEmbeddableEntity(project, entityName, packageName);
   }

   public JavaResource createEmptyEnum(Project project, String enumName) throws IOException
   {
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      JavaEnumSource enumSource = Roaster.create(JavaEnumSource.class).setName(enumName);
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + ".model";
      enumSource.setPackage(packageName);
      return javaSourceFacet.saveJavaSource(enumSource);
   }

   public Project refreshProject(Project project)
   {
      return projectFactory.findProject(project.getRoot());
   }
}