/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.validation.provider;

import java.util.Set;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.spec.javaee.descriptor.ValidationDescriptor;

/**
 * @author Kevin Pollet
 */
public interface ValidationProvider
{
   ValidationDescriptor getDefaultDescriptor();

   Set<Dependency> getDependencies();

   Set<Dependency> getAdditionalDependencies();
}
