/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.validation.provider;

import static java.util.Collections.unmodifiableSet;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.spec.javaee.descriptor.ValidationDescriptor;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

/**
 * @author Kevin Pollet
 */
public class ApacheBeanValidationProvider implements ValidationProvider
{
    private final ValidationDescriptor defaultDescriptor;
    private final Set<Dependency> dependencies;

    public ApacheBeanValidationProvider()
    {
        // define apache bean validation default descriptor file
        this.defaultDescriptor = Descriptors.create(ValidationDescriptor.class)
                .setDefaultProvider("org.apache.bval.jsr303.ApacheValidationProvider")
                .setMessageInterpolator("org.apache.bval.jsr303.DefaultMessageInterpolator")
                .setTraversableResolver("org.apache.bval.jsr303.resolver.DefaultTraversableResolver")
                .setConstraintValidatorFactory("org.apache.bval.jsr303.DefaultConstraintValidatorFactory");

        // add apache bean validation dependencies
        final DependencyBuilder apacheBeanValidation = DependencyBuilder.create()
                .setGroupId("org.apache.bval")
                .setArtifactId("org.apache.bval.bundle");

        final Set<Dependency> tmpSet = new LinkedHashSet<Dependency>();
        tmpSet.add(apacheBeanValidation);

        this.dependencies = unmodifiableSet(tmpSet);
    }

    @Override
    public ValidationDescriptor getDefaultDescriptor()
    {
        return defaultDescriptor;
    }

    @Override
    public Set<Dependency> getDependencies()
    {
        return dependencies;
    }

    @Override
    public Set<Dependency> getAdditionalDependencies()
    {
        return Collections.emptySet();
    }
}
