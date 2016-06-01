/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jaxws.ui;

import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_SOAP_PACKAGE;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.jaxws.JAXWSFacet;
import org.jboss.forge.addon.javaee.jaxws.ui.setup.JAXWSSetupWizard;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 * 
 */
public abstract class AbstractJAXWSCommand<T extends JavaSource<?>> extends AbstractJavaSourceCommand<T>
         implements PrerequisiteCommandsProvider
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .category(Categories.create(Categories.create("Java EE"), "JAX-WS"));
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected String calculateDefaultPackage(UIContext context)
   {
      return getSelectedProject(context).getFacet(JavaSourceFacet.class).getBasePackage() + "."
               + DEFAULT_SOAP_PACKAGE;
   }

   @Override
   public NavigationResult getPrerequisiteCommands(UIContext context)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      Project project = getSelectedProject(context);
      if (project != null)
      {
         if (!project.hasFacet(JAXWSFacet.class))
         {
            builder.add(JAXWSSetupWizard.class);
         }
      }
      return builder.build();
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}