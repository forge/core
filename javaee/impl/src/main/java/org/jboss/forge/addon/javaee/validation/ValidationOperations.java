/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.facets.ValidationFacet;
import org.jboss.forge.addon.javaee.validation.provider.ValidationProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.validationConfiguration11.ValidationConfigurationDescriptor;

public class ValidationOperations
{

   @Inject
   private DependencyInstaller installer;
   
   @Inject
   private FacetFactory facetFactory;

   public void setup(Project project, ValidationProvider provider, boolean provided, String messageInterpolator,
            String traversableResolver, String constraintValidatorFactory) throws Exception
   {
      if (project != null)
      {
         ValidationFacet facet = facetFactory.install(project, ValidationFacet.class);
         
         String scopeType = provided?"PROVIDED":"COMPILE";
         installDependencies(project, provider.getDependencies(), scopeType);
         installDependencies(project, provider.getAdditionalDependencies(), scopeType);
         
         if (provider.getDefaultDescriptor() != null)
         {
            final ValidationConfigurationDescriptor providerDescriptor = provider.getDefaultDescriptor();
            final ValidationConfigurationDescriptor descriptor = Descriptors.create(ValidationConfigurationDescriptor.class);
            String defaultProvider = providerDescriptor.getDefaultProvider();
            if (defaultProvider != null && !defaultProvider.isEmpty())
            {
               descriptor.defaultProvider(defaultProvider);
            }
            if (messageInterpolator != null)
            {
               descriptor.messageInterpolator(messageInterpolator);
            }
            if (traversableResolver != null)
            {
               descriptor.traversableResolver(traversableResolver);
            }
            if (constraintValidatorFactory != null)
            {
               descriptor.constraintValidatorFactory(constraintValidatorFactory);
            }

            project.getFacet(ValidationFacet.class).saveConfig(descriptor);
         }
         ValidationConfigurationDescriptor config = facet.getConfig();

         facet.saveConfig(config);
      }
   }
   
   private void installDependencies(Project project,final Set<Dependency> dependencies, String scopeType)
   {
      for (Dependency dep : dependencies)
      {
         if (!installer.isInstalled(project, dep))
         {
             dep = DependencyBuilder.create(dep).setScopeType(scopeType);
             installer.install(project, dep);
         }
      }
   }

}
