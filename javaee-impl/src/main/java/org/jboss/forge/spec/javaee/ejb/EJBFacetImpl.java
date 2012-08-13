/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.ejb;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.EJBFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.ejb")
public class EJBFacetImpl extends BaseJavaEEFacet implements EJBFacet
{
   @Inject
   public EJBFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList(
               (Dependency) DependencyBuilder.create("org.jboss.spec.javax.ejb:jboss-ejb-api_3.1_spec")
               );
   }
}
