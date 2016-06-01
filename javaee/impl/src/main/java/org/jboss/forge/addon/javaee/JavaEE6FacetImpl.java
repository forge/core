/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.facets.JavaEE6Facet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

/**
 * Implementation for the {@link JavaEE6Facet}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(DependencyFacet.class)
public class JavaEE6FacetImpl extends AbstractJavaEEFacet implements JavaEE6Facet
{

   @Inject
   public JavaEE6FacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "Java EE";
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("6");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<>();
      Dependency JAVAEE6_PROVIDED = DependencyBuilder.create(JAVAEE6).setScopeType("provided");
      result.put(JAVAEE6_PROVIDED, Arrays.asList(JAVAEE6_PROVIDED));
      return result;
   }

   @Override
   public Stack getStack()
   {
      return JavaEE6Facet.STACK;
   }

   @Override
   public boolean uninstall()
   {
      DependencyFacet facet = getFaceted().getFacet(DependencyFacet.class);
      facet.removeDependency(JAVAEE6);
      facet.removeManagedDependency(JAVAEE6);
      return true;
   }
}
