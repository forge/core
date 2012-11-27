/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.container;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyNode;

public class MavenDependencyNode implements DependencyNode
{
   private Dependency dependency;
   private List<DependencyNode> children = new ArrayList<DependencyNode>();

   public MavenDependencyNode(Dependency dependency)
   {
      super();
      this.dependency = dependency;
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
      StringBuilder builder = new StringBuilder();
      prettyPrint(builder, 0);
      return builder.toString();
   }

   protected void prettyPrint(StringBuilder builder, int depth)
   {
      for (int i = 0; i < depth; i++)
      {
         builder.append("\t");
      }
      builder.append("|-").append(dependency).append("\n");
      for (DependencyNode child : getChildren())
      {
         ((MavenDependencyNode) child).prettyPrint(builder, depth + 1);
      }
   }

}
