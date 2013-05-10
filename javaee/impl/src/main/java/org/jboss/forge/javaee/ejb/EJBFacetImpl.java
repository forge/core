/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.javaee.ejb;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.javaee.AbstractJavaEEFacet;
import org.jboss.forge.javaee.spec.EJBFacet;
import org.jboss.forge.projects.dependencies.DependencyInstaller;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EJBFacetImpl extends AbstractJavaEEFacet implements EJBFacet
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
