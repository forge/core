/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee.validation;

import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
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

import static org.jboss.forge.shell.PromptType.JAVA_CLASS;
import static org.jboss.shrinkwrap.descriptor.api.Descriptors.create;

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
    private final DependencyFacet dependencyFacet;
    private final ShellPrompt prompt;

    @Inject
    public ValidationPlugin(Project project, Event<InstallFacets> request, BeanManager beanManager, ShellPrompt prompt)
    {
        this.project = project;
        this.beanManager = beanManager;
        this.request = request;
        this.dependencyFacet = project.getFacet(DependencyFacet.class);
        this.prompt = prompt;
    }

    @Command(value = "setup", help = "Setup validation for this project")
    public void setup(@Option(name = "provider", defaultValue = "HIBERNATE_VALIDATOR", required = true) BVProvider provider,
                      @Option(name = "messageInterpolator", type = JAVA_CLASS) String messageInterpolator,
                      @Option(name = "traversableResolver", type = JAVA_CLASS) String traversableResolver,
                      @Option(name = "constraintValidatorFactory", type = JAVA_CLASS) String constraintValidatorFactory)
    {
        // instantiates the validation provider specified by the user
        final ValidationProvider validationProvider = provider.getValidationProvider(beanManager);

        installValidationFacet();
        installDependencies(validationProvider.getDependencies());

        if (!validationProvider.getAdditionalDependencies().isEmpty())
        {
            if (prompt.promptBoolean("Would you install " + provider.getName() + " additional dependencies?" ,false)){
                installDependencies(validationProvider.getAdditionalDependencies());
            }
        }

        // generates the default provider validation configuration file
        final ValidationDescriptor providerDescriptor = validationProvider.getDefaultDescriptor();
        final ValidationDescriptor descriptor = create(ValidationDescriptor.class)
                .defaultProvider(providerDescriptor.getDefaultProvider())
                .messageInterpolator(messageInterpolator == null ? providerDescriptor.getMessageInterpolator() : messageInterpolator)
                .traversableResolver(traversableResolver == null ? providerDescriptor.getTraversableResolver() : traversableResolver)
                .constraintValidatorFactory(constraintValidatorFactory == null ? providerDescriptor.getConstraintValidatorFactory() : constraintValidatorFactory);

        project.getFacet(ValidationFacet.class).saveConfig(descriptor);
    }

    private void installValidationFacet()
    {
        if (!project.hasFacet(ValidationFacet.class))
        {
            request.fire(new InstallFacets(ValidationFacet.class));
        }
    }

    private void installDependencies(Set<Dependency> dependencies)
    {
        for (Dependency oneDependency : dependencies)
        {
            // let the user the choice of the version
            final List<Dependency> versions = dependencyFacet.resolveAvailableVersions(oneDependency);
            final Dependency selected = prompt.promptChoiceTyped("Which version of " + oneDependency.getArtifactId() + " would you like to use?", versions, versions.get(versions.size() - 1));
            if (!dependencyFacet.hasDependency(selected))
            {
                dependencyFacet.addDependency(selected);
            }
        }
    }
}
