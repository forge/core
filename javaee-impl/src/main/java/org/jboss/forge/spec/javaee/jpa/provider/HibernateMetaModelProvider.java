/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.provider;

import org.apache.maven.model.Repository;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.spec.javaee.jpa.api.MetaModelProvider;

public class HibernateMetaModelProvider implements MetaModelProvider
{

   @Override
   public Dependency getAptDependency()
   {
      return DependencyBuilder.create()
               .setGroupId("org.hibernate")
               .setArtifactId("hibernate-jpamodelgen");
   }

   @Override
   public String getProcessor()
   {
      return "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor";
   }

   @Override
   public String getCompilerArguments()
   {
      return null;
   }

   @Override
   public Repository getAptPluginRepository()
   {
      return null;
   }

}
