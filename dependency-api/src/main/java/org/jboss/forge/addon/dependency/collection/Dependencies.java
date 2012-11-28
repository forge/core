/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyNode;

/**
 * Provides utility methods for working with {@link Dependency} and {@link DependencyNode} objects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public final class Dependencies
{

   private Dependencies()
   {
   }

   /**
    * Returns the {@link DependencyNode} objects that satisfy the filter. The method
    * {@link DependencyNode#getChildren()} is called for each found node.
    *
    * @param root a {@link DependencyNode} as the starting point
    * @param filter the {@link DependencyNodeFilter} being used
    * @return list of matched elements
    *
    */
   public static List<DependencyNode> collect(DependencyNode root, DependencyNodeFilter filter)
   {
      List<DependencyNode> result = new ArrayList<DependencyNode>();
      collect(root, filter, result);
      return result;

   }

   /**
    * Returns the {@link DependencyNode} objects that satisfy the filter. The method
    * {@link DependencyNode#getChildren()} is called for each found node.
    *
    * @param root a {@link DependencyNode} as the starting point
    * @param filter the {@link DependencyNodeFilter} being used
    * @param outputList the {@link Collection} with the output
    *
    */
   public static void collect(DependencyNode node, DependencyNodeFilter filter, Collection<DependencyNode> outputList)
   {
      if (filter.accept(node))
      {
         outputList.add(node);
      }
      for (DependencyNode child : node.getChildren())
      {
         collect(child, filter, outputList);
      }
   }

   /**
    * Prints a tree-like structure for this object
    *
    * @param root
    * @return
    */
   public static CharSequence prettyPrint(DependencyNode root)
   {
      StringBuilder sb = new StringBuilder();
      prettyPrint(root, sb, 0);
      return sb;
   }

   private static void prettyPrint(DependencyNode node, StringBuilder builder, int depth)
   {
      for (int i = 0; i < depth; i++)
      {
         builder.append(" ");
      }
      if (depth > 0)
      {
         builder.append("|-");
      }
      builder.append(node.getDependency()).append("\n");
      for (DependencyNode child : node.getChildren())
      {
         prettyPrint(child, builder, depth + 1);
      }
   }

}
