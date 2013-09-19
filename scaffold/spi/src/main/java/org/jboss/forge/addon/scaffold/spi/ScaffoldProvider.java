/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.spi;

import java.util.List;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * Provides an implementation of Scaffolding for various UI code generation operations.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ScaffoldProvider extends ProjectFacet, UIValidator
{
   /**
    * Return the name for this {@link ScaffoldProvider}
    * 
    * Ex: faces
    */
   String getName();

   /**
    * Return the description for this {@link ScaffoldProvider}
    * 
    * Ex: JavaServer Faces
    */
   String getDescription();

   /**
    * Set up this scaffold provider, installing any necessary {@link Facet} or {@link Plugin} implementations as
    * necessary. Install the templates in the provider to the src/main/templates directory of the project.
    */
   List<Resource<?>> setup(Project project, ScaffoldContext scaffoldContext);

   /**
    * Generate a set of create, read, update, delete pages for the given collection of resources {@link Resource}. Note
    * that any collection of Resource instances can be provided to the {@link ScaffoldProvider}. It is the
    * responsibility of the ScaffoldProvider to verify whether it can act on the provided resource.
    */
   List<Resource<?>> generateFrom(Iterable<Resource<?>> resources, ScaffoldContext scaffoldContext);

   /**
    * Needs overwrite confirmation ?
    * 
    * @param scaffoldContext
    * @return
    */
   boolean needsOverwriteConfirmation(ScaffoldContext scaffoldContext);

   /**
    * Return the {@link UIWizardStep} class that begins the scaffold setup of this type, if any.
    */
   Class<? extends UIWizardStep> getSetupFlow();

   AccessStrategy getAccessStrategy();

}
