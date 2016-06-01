/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import static org.jboss.forge.addon.git.constants.GitConstants.GITIGNORE;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.git.GitUtils;
import org.jboss.forge.addon.git.gitignore.resources.GitIgnoreResource;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

abstract class AbstractGitCommand extends AbstractProjectCommand
{

   private ProjectFactory projectFactory;
   private InputComponentFactory inputComponentFactory;
   private GitUtils gitUtils;
   private ResourceFactory resourceFactory;
   private FacetFactory facetFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).category(Categories.create("SCM / GIT"));
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      if (projectFactory == null)
      {
         projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      }
      return projectFactory;
   }

   protected GitIgnoreResource gitIgnoreResource(UIContext context)
   {
      GitIgnoreResource resource = getSelectedProject(context).getRoot().reify(DirectoryResource.class).getChildOfType(
               GitIgnoreResource.class,
               GITIGNORE);
      if (resource == null || !resource.exists())
      {
         resource.createNewFile();
      }
      return resource;
   }

   protected boolean isGitIgnoreSelected(UIContext context)
   {
      return context.getInitialSelection().get() instanceof GitIgnoreResource;
   }

   protected InputComponentFactory getInputComponentFactory()
   {
      if (inputComponentFactory == null)
      {
         inputComponentFactory = SimpleContainer.getServices(getClass().getClassLoader(), InputComponentFactory.class)
                  .get();
      }
      return inputComponentFactory;
   }

   protected GitUtils getGitUtils()
   {
      if (gitUtils == null)
      {
         gitUtils = SimpleContainer.getServices(getClass().getClassLoader(), GitUtils.class).get();
      }
      return gitUtils;
   }

   protected ResourceFactory getResourceFactory()
   {
      if (resourceFactory == null)
      {
         resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
      }
      return resourceFactory;
   }

   protected FacetFactory getFacetFactory()
   {
      if (facetFactory == null)
      {
         facetFactory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
      }
      return facetFactory;
   }
}
