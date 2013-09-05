package org.jboss.forge.addon.javaee.validation;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.descriptor.ValidationDescriptor;
import org.jboss.forge.addon.javaee.facets.ValidationFacet;
import org.jboss.forge.addon.javaee.validation.provider.ValidationProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

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
            final ValidationDescriptor providerDescriptor = provider.getDefaultDescriptor();
            final ValidationDescriptor descriptor = Descriptors.create(ValidationDescriptor.class);
            String defaultProvider = providerDescriptor.getDefaultProvider();
            if (defaultProvider != null && !defaultProvider.isEmpty())
            {
               descriptor.setDefaultProvider(defaultProvider);
            }
            if (messageInterpolator != null)
            {
               descriptor.setMessageInterpolator(messageInterpolator);
            }
            if (traversableResolver != null)
            {
               descriptor.setTraversableResolver(traversableResolver);
            }
            if (constraintValidatorFactory != null)
            {
               descriptor.setConstraintValidatorFactory(constraintValidatorFactory);
            }

            project.getFacet(ValidationFacet.class).saveConfig(descriptor);
         }
         ValidationDescriptor config = facet.getConfig();

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
