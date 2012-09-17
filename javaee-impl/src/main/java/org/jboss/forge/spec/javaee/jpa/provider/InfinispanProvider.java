/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.provider;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.spec.javaee.jpa.api.JPADataSource;
import org.jboss.forge.spec.javaee.jpa.api.MetaModelProvider;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceProvider;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InfinispanProvider implements PersistenceProvider
{
   @Override
   public PersistenceUnitDef configure(final PersistenceUnitDef unit, final JPADataSource ds)
   {
      unit.includeUnlistedClasses();
      unit.property("hibernate.dialect", "org.hibernate.ogm.dialect.NoopDialect");
      return unit;
   }

   @Override
   public String getProvider()
   {
      return "org.hibernate.ogm.HibernateOgmPersistence";
   }

   @Override
   public List<Dependency> listDependencies()
   {
      return Arrays.asList((Dependency) DependencyBuilder.create("org.hibernate.ogm:hibernate-ogm-core"),
               (Dependency) DependencyBuilder.create("org.hibernate:hibernate-search"));
   }

   @Override
   public MetaModelProvider getMetaModelProvider()
   {
      return new HibernateMetaModelProvider();
   }
}
