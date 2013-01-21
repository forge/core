/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.javaee.ServletFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.jaxrs")
@RequiresFacet(ServletFacet.class)
public class RestFacetImpl extends BaseJavaEEFacet implements RestFacet
{

   @Inject
   private Configuration configuration;

   @Inject
   public RestFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public boolean install()
   {
      return super.install();
   }

   @Override
   public boolean isInstalled()
   {
      return super.isInstalled();
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList(
              (Dependency) DependencyBuilder.create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec")
      );
   }

   @Override
   public String getApplicationPath()
   {
      return configuration.getString(RestFacet.ROOTPATH);
   }
}
