/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.locator.ProjectLocator;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.forge.shell.util.ConstraintInspector;
import org.jboss.forge.shell.util.ResourceUtil;

/**
 * Responsible for instantiating project instances through CDI.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ProjectFactory
{
   private final FacetFactory facetFactory;
   private List<ProjectLocator> locators;
   private final BeanManager manager;
   private final Instance<ProjectLocator> locatorInstance;

   @Inject
   public ProjectFactory(final FacetFactory facetFactory, final BeanManager manager,
            final Instance<ProjectLocator> locatorInstance)
   {
      this.facetFactory = facetFactory;
      this.locatorInstance = locatorInstance;
      this.manager = manager;
   }

   public void init()
   {
      if (locators == null || locators.isEmpty())
      {
         Iterator<ProjectLocator> iterator = locatorInstance.iterator();
         List<ProjectLocator> result = new ArrayList<ProjectLocator>();
         while (iterator.hasNext())
         {
            ProjectLocator element = BeanManagerUtils.getContextualInstance(manager, iterator.next().getClass());
            result.add(element);
         }
         this.locators = result;
      }
   }

   public DirectoryResource findProjectRootRecusively(final DirectoryResource currentDirectory)
   {
      DirectoryResource root = null;
      List<ProjectLocator> locators = getLocators();
      for (ProjectLocator locator : locators)
      {
         root = locateRecursively(currentDirectory, locator);
         if (root != null)
         {
            break;
         }
      }
      return root;
   }

   public DirectoryResource locateRecursively(final DirectoryResource startingDirectory, final ProjectLocator locator)
   {
      DirectoryResource root = startingDirectory;

      while (!locator.containsProject(root) && (root.getParent() instanceof DirectoryResource))
      {
         root = (DirectoryResource) root.getParent();
      }

      if (!locator.containsProject(root))
      {
         root = null;
      }

      return root;
   }

   public Project findProjectRecursively(final DirectoryResource startingPath)
   {
      Project project = null;
      List<ProjectLocator> locators = getLocators();
      for (ProjectLocator locator : locators)
      {
         DirectoryResource root = locateRecursively(startingPath, locator);

         if ((root != null) && locator.containsProject(root))
         {
            project = locator.createProject(root);
            if (project != null)
            {
               break;
            }
         }
      }

      if (project != null)
      {
         registerFacets(project);
      }

      return project;
   }

   public Project createProject(final DirectoryResource root, final Class<? extends Facet>... facetTypes)
   {
      Project project = null;
      List<ProjectLocator> locators = getLocators();
      for (ProjectLocator locator : locators)
      {
         if (root != null)
         {
            project = locator.createProject(root);
            if (project != null)
            {
               break;
            }
         }
      }

      if (project != null)
      {
         for (Class<? extends Facet> type : facetTypes)
         {
            installSingleFacet(project, type);
         }

         registerFacets(project);
      }
      return project;
   }

   public void installSingleFacet(final Project project, final Class<? extends Facet> type)
   {
      Facet facet = facetFactory.getFacet(type);

      List<Class<? extends Facet>> dependencies = ConstraintInspector.getFacetDependencies(type);
      if (dependencies != null)
      {
         for (Class<? extends Facet> dep : dependencies)
         {
            if (!project.hasFacet(dep))
            {
               installSingleFacet(project, dep);
            }
         }
      }

      project.installFacet(facet);
   }

   private void registerFacets(final Project project)
   {
      if (project != null)
      {
         Set<Class<? extends Facet>> facets = facetFactory.getFacetTypes();

         for (Class<? extends Facet> facet : facets)
         {
            registerSingleFacet(project, facet);
         }
      }
   }

   public void registerSingleFacet(final Project project, final Class<? extends Facet> type)
   {
      Facet facet = facetFactory.getFacet(type);
      registerSingleFacet(project, facet);
   }

   private void registerSingleFacet(final Project project, final Facet facet)
   {
      List<Class<? extends Facet>> dependencies = ConstraintInspector.getFacetDependencies(facet.getClass());
      if (dependencies != null)
      {
         for (Class<? extends Facet> dep : dependencies)
         {
            if (!project.hasFacet(dep))
            {
               Facet depFacet = facetFactory.getFacet(dep);
               registerSingleFacet(project, depFacet);
               if (!project.hasFacet(dep))
               {
                  return;
               }
            }
         }
      }

      project.registerFacet(facet);
   }

   /**
    * An exception-safe method of determining whether a directory contains a project.
    */
   public boolean containsProject(final DirectoryResource dir)
   {
      Project project = findProject(dir);
      return project != null;
   }

   public Project findProject(final DirectoryResource dir)
   {
      Project project = null;
      if (dir != null)
      {
         List<ProjectLocator> locators = getLocators();
         for (ProjectLocator locator : locators)
         {
            if (locator.containsProject(ResourceUtil.getContextDirectory(dir)))
            {
               project = locator.createProject(dir);
               break;
            }
         }

         if (project != null)
         {
            registerFacets(project);
         }
      }
      return project;
   }

   private List<ProjectLocator> getLocators()
   {
      init();
      return locators;
   }
}
