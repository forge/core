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

public class OpenJPAMetaModelProvider implements MetaModelProvider
{

   @Override
   public Dependency getAptDependency()
   {
      return DependencyBuilder.create()
               .setGroupId("org.apache.openjpa")
               .setArtifactId("openjpa-persistence");
   }

   @Override
   public String getProcessor()
   {
      return "org.apache.openjpa.persistence.meta.AnnotationProcessor6";
   }

   @Override
   public String getCompilerArguments()
   {
      return "-Aopenjpa.metamodel=true";
   }

   @Override
   public DependencyRepository getAptPluginRepository()
   {
      return null;
   }

}
