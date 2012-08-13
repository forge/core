/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.soap;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.spec.javaee.SoapFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.jaxws")
@RequiresFacet(ServletFacet.class)
public class SoapFacetImpl extends BaseJavaEEFacet implements SoapFacet
{
   @Inject
   public SoapFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList(
               (Dependency) DependencyBuilder.create("org.jboss.spec.javax.xml.bind:jboss-jaxb-api_2.2_spec"),
               (Dependency) DependencyBuilder.create("org.jboss.spec.javax.xml.rpc:jboss-jaxrpc-api_1.1_spec"),
               (Dependency) DependencyBuilder.create("org.jboss.spec.javax.xml.soap:jboss-saaj-api_1.3_spec"),
               (Dependency) DependencyBuilder.create("org.jboss.spec.javax.xml.ws:jboss-jaxws-api_2.2_spec")
               );
   }
}
