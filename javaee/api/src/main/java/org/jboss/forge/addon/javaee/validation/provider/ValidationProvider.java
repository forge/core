/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.provider;

import java.util.Set;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.shrinkwrap.descriptor.api.validationConfiguration11.ValidationConfigurationDescriptor;

/**
 * @author Kevin Pollet
 */
public interface ValidationProvider
{
   String getName();

   ValidationConfigurationDescriptor getDefaultDescriptor();

   Set<Dependency> getDependencies();

   Set<Dependency> getAdditionalDependencies();
}
