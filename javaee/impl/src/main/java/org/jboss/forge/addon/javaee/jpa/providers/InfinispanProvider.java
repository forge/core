/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.providers;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.MetaModelProvider;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;
import org.jboss.shrinkwrap.descriptor.api.persistence.PropertyCommon;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InfinispanProvider implements PersistenceProvider
{
   public static final String JPA_PROVIDER = "org.hibernate.ogm.HibernateOgmPersistence";

   @Override
   @SuppressWarnings("rawtypes")
   public PersistenceUnitCommon configure(PersistenceUnitCommon unit, JPADataSource ds, Project project)
   {
      unit.excludeUnlistedClasses(Boolean.FALSE);
      PropertyCommon dialectProperty = unit.getOrCreateProperties().createProperty();
      dialectProperty.name("hibernate.dialect").value("org.hibernate.ogm.dialect.NoopDialect");
      return unit;
   }

   @Override
   public void validate(JPADataSource dataSource) throws Exception
   {
      // NOOP
   }

   @Override
   public String getProvider()
   {
      return JPA_PROVIDER;
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

   @Override
   public String getName()
   {
      return "Infinispan";
   }
}
