/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.spi;

import java.util.List;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.result.NavigationResult;

/**
 * Provides an implementation of Scaffolding for various UI code generation operations.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ScaffoldProvider
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
    * Set up this scaffold provider, installing any necessary {@link Facet} implementations as necessary. Install the
    * templates in the provider to the src/main/templates directory of the project.
    *
    * @param setupContext The execution context for the setup stage
    */
   List<Resource<?>> setup(ScaffoldSetupContext setupContext);

   /**
    * Verifies whether the provider was setup or not. This is used to determine whether the setup method should be
    * executed.
    *
    * @param setupContext The execution context for the setup stage
    * @return boolean value indicating whether the provider was setup
    */
   boolean isSetup(ScaffoldSetupContext setupContext);

   /**
    * Generate a set of create, read, update, delete pages for the given collection of {@link Resource}s present in the
    * {@link ScaffoldGenerationContext}. Note that any collection of Resource instances can be provided to the
    * {@link ScaffoldProvider}. It is the responsibility of the ScaffoldProvider to verify whether it can act on the
    * provided resource.
    *
    * @param generationContext The execution context for the generation stage
    */
   List<Resource<?>> generateFrom(ScaffoldGenerationContext generationContext);

   /**
    * Return the {@link List} of {@link UICommand} classes that begins the scaffold setup of this type, if any.
    * @param setupContext The execution context for the setup stage
    */
   NavigationResult getSetupFlow(ScaffoldSetupContext setupContext);

   /**
    * Return the {@link List} of {@link UICommand} classes that begins the scaffold generation of this type, if any.
    * @param generationContext The execution context for the generation stage
    */
   NavigationResult getGenerationFlow(ScaffoldGenerationContext generationContext);

   AccessStrategy getAccessStrategy();

}
