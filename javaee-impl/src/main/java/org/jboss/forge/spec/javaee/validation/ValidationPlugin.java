/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.validation;

import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.spec.javaee.ValidationFacet;
import org.jboss.forge.spec.javaee.descriptor.ValidationDescriptor;
import org.jboss.forge.spec.javaee.validation.provider.BVProvider;
import org.jboss.forge.spec.javaee.validation.provider.ValidationProvider;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

/**
 * @author Kevin Pollet
 */
@Alias("validation")
@RequiresProject
@RequiresFacet(DependencyFacet.class)
public class ValidationPlugin implements Plugin
{
   private final Project project;
   private final BeanManager beanManager;
   private final Event<InstallFacets> request;
   private final ShellPrompt prompt;
   private final DependencyInstaller installer;

   @Inject
   public ValidationPlugin(final Project project, final Event<InstallFacets> request, final BeanManager beanManager,
            final ShellPrompt prompt, final DependencyInstaller installer)
   {
      this.project = project;
      this.beanManager = beanManager;
      this.request = request;
      this.prompt = prompt;
      this.installer = installer;
   }

   @Command(value = "setup", help = "Setup validation for this project")
   public void setup(
            @Option(name = "provider", defaultValue = "HIBERNATE_VALIDATOR", required = true) final BVProvider providerType,
            @Option(name = "messageInterpolator", type = PromptType.JAVA_CLASS) final String messageInterpolator,
            @Option(name = "traversableResolver", type = PromptType.JAVA_CLASS) final String traversableResolver,
            @Option(name = "constraintValidatorFactory", type = PromptType.JAVA_CLASS) final String constraintValidatorFactory)
   {
      // instantiates the validation provider specified by the user
      final ValidationProvider provider = providerType.getValidationProvider(beanManager);

      if (!project.hasFacet(ValidationFacet.class))
      {
         request.fire(new InstallFacets(ValidationFacet.class));
      }

      installDependencies(provider.getDependencies());

      if (!provider.getAdditionalDependencies().isEmpty())
      {
         if (prompt.promptBoolean("Would you install " + providerType.getName() + " additional dependencies?", false)) {
            installDependencies(provider.getAdditionalDependencies());
         }
      }

      if (provider.getDefaultDescriptor() != null)
      {
         final ValidationDescriptor providerDescriptor = provider.getDefaultDescriptor();
         final ValidationDescriptor descriptor = Descriptors.create(ValidationDescriptor.class)
                  .setDefaultProvider(providerDescriptor.getDefaultProvider())
                  .setMessageInterpolator( messageInterpolator == null ? providerDescriptor.getMessageInterpolator() : messageInterpolator)
                  .setTraversableResolver( traversableResolver == null ? providerDescriptor.getTraversableResolver() : traversableResolver)
                  .setConstraintValidatorFactory( constraintValidatorFactory == null ? providerDescriptor.getConstraintValidatorFactory() : constraintValidatorFactory);
         
         project.getFacet(ValidationFacet.class).saveConfig(descriptor);
      }

   }

   private void installDependencies(final Set<Dependency> dependencies)
   {
      for (Dependency dep : dependencies)
      {
         if (!installer.isInstalled(project, dep))
         {
             dep = DependencyBuilder.create(dep).setScopeType(promptForScope(dep));
             installer.install(project, dep);
         }
      }
   }

    private ScopeType promptForScope(Dependency dep) {
        boolean answer = prompt.promptBoolean("Should the dependency be packaged with your application (not provided by the server)?", false);
        return answer ? ScopeType.COMPILE : ScopeType.PROVIDED;
    }
}
