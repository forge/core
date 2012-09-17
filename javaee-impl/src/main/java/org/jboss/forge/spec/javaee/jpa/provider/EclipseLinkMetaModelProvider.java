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

public class EclipseLinkMetaModelProvider implements MetaModelProvider
{

   @Override
   public Dependency getAptDependency()
   {
      return DependencyBuilder.create()
               .setGroupId("org.eclipse.persistence")
               .setArtifactId("eclipselink")
               .setVersion("2.4.0");
   }

   @Override
   public String getProcessor()
   {
      return "org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor";
   }

   @Override
   public String getCompilerArguments()
   {
      return "-Aeclipselink.persistencexml=src/main/resources/META-INF/persistence.xml";
   }

   @Override
   public Repository getAptPluginRepository()
   {
      Repository repo = new Repository();
      repo.setName("EclipseLink");
      repo.setUrl("http://download.eclipse.org/rt/eclipselink/maven.repo");
      return repo;
   }

}
