/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.javaee.descriptor.ValidationDescriptor;
import org.jboss.forge.addon.javaee.validation.provider.ValidationProvider;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

/**
 * @author Kevin Pollet
 */
public class HibernateValidatorProvider implements ValidationProvider
{
   private static final String PROVIDER_NAME = "Hibernate Validator";
   private final ValidationDescriptor defaultDescriptor;
   private final Set<Dependency> dependencies;

   public HibernateValidatorProvider()
   {
      // define hibernate validator default descriptor file
      this.defaultDescriptor = Descriptors.create(ValidationDescriptor.class)
               .setDefaultProvider("org.hibernate.validator.HibernateValidator")
               .setMessageInterpolator("org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator")
               .setTraversableResolver("org.hibernate.validator.engine.resolver.DefaultTraversableResolver")
               .setConstraintValidatorFactory("org.hibernate.validator.engine.ConstraintValidatorFactoryImpl");

      // add hibernate validator dependencies
      final DependencyBuilder hibernateValidator = DependencyBuilder.create()
               .setGroupId("org.hibernate")
               .setArtifactId("hibernate-validator")
               .setScopeType("provided");

      final Set<Dependency> dependenciesTmpSet = new LinkedHashSet<Dependency>();
      dependenciesTmpSet.add(hibernateValidator);

      this.dependencies = unmodifiableSet(dependenciesTmpSet);
   }
   
   @Override
   public String getName()
   {
      return PROVIDER_NAME;
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
