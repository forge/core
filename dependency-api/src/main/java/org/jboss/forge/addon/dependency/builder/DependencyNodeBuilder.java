/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency.builder;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.collection.Dependencies;

/**
 * Creates a {@link DependencyNode} object
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class DependencyNodeBuilder implements DependencyNode
{
   private Dependency dependency;
   private List<DependencyNode> children = new ArrayList<DependencyNode>();

   private DependencyNodeBuilder(Dependency dependency)
   {
      this.dependency = dependency;
   }

   public static DependencyNodeBuilder create(Dependency dependency)
   {
      DependencyNodeBuilder builder = new DependencyNodeBuilder(dependency);
      return builder;
   }

   public DependencyNodeBuilder newChild(Dependency dependency)
   {
      DependencyNodeBuilder builder = DependencyNodeBuilder.create(dependency);
      children.add(builder);
      return builder;
   }

   @Override
   public Dependency getDependency()
   {
      return dependency;
   }

   @Override
   public List<DependencyNode> getChildren()
   {
      return children;
   }

   @Override
   public String toString()
   {
      return Dependencies.prettyPrint(this).toString();
   }
}
