/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.providers;

import static java.util.Collections.unmodifiableSet;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.javaee.validation.provider.ValidationProvider;
import org.jboss.shrinkwrap.descriptor.api.validationConfiguration11.ValidationConfigurationDescriptor;

/**
 * @author Kevin Pollet
 */
public class ApacheBeanValidationProvider implements ValidationProvider
{
   private static final String PROVIDER_NAME = "Apache Bean Validation";
   private final ValidationConfigurationDescriptor defaultDescriptor;
   private final Set<Dependency> dependencies;

   public ApacheBeanValidationProvider()
   {
      // define apache bean validation default descriptor file
      this.defaultDescriptor = Descriptors.create(ValidationConfigurationDescriptor.class)
               .defaultProvider("org.apache.bval.jsr303.ApacheValidationProvider")
               .messageInterpolator("org.apache.bval.jsr303.DefaultMessageInterpolator")
               .traversableResolver("org.apache.bval.jsr303.resolver.DefaultTraversableResolver")
               .constraintValidatorFactory("org.apache.bval.jsr303.DefaultConstraintValidatorFactory");

      // add apache bean validation dependencies
      final DependencyBuilder apacheBeanValidation = DependencyBuilder.create()
               .setGroupId("org.apache.bval")
               .setArtifactId("org.apache.bval.bundle");

      final Set<Dependency> tmpSet = new LinkedHashSet<>();
      tmpSet.add(apacheBeanValidation);

      this.dependencies = unmodifiableSet(tmpSet);
   }

   @Override
   public String getName()
   {
      return PROVIDER_NAME;
   }

   @Override
   public ValidationConfigurationDescriptor getDefaultDescriptor()
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
