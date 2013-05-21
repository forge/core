/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.providers;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.jpa.MetaModelProvider;

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
   public DependencyRepository getAptPluginRepository()
   {
      return new DependencyRepository("EclipseLink",
               "http://download.eclipse.org/rt/eclipselink/maven.repo");
   }

}
