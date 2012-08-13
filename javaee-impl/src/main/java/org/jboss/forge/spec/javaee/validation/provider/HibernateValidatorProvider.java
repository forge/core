/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.validation.provider;

import static java.util.Collections.unmodifiableSet;
import static org.jboss.forge.project.dependencies.ScopeType.PROVIDED;

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
public class HibernateValidatorProvider implements ValidationProvider
{
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
               .setVersion("[4.1.0.Final,)")
               .setScopeType(PROVIDED);

      final Set<Dependency> dependenciesTmpSet = new LinkedHashSet<Dependency>();
      dependenciesTmpSet.add(hibernateValidator);

      this.dependencies = unmodifiableSet(dependenciesTmpSet);
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
