/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;

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
   public static final String FORGE_ADDON_CLASSIFIER = "forge-addon";

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
   public static List<DependencyNode> collect(Iterator<DependencyNode> nodeIterator, DependencyNodeFilter filter)
   {
      List<DependencyNode> result = new ArrayList<DependencyNode>();
      while (nodeIterator.hasNext())
      {
         DependencyNode node = nodeIterator.next();
         if (filter.accept(node))
         {
            result.add(node);
         }
      }
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

   /**
    * Creates and returns an iterator that traverses the subtree rooted at this node in depth-first order. The first
    * node returned by {@link Iterator#next()} is the leftmost leaf.
    * <P>
    *
    * Modifying the tree by inserting, removing, or moving a node invalidates any iterators created before the
    * modification.
    *
    * @see #breadthFirstIterator(DependencyNode)
    * @see #preorderIterator(DependencyNode)
    * @return an iterator for traversing the tree in depth-first order
    */
   public static Iterator<DependencyNode> depthFirstIterator(DependencyNode dependencyNode)
   {
      return new PostorderIterator(dependencyNode);
   }

   /**
    * Creates and returns an iterator that traverses the subtree rooted at this node in breadth-first order. The first
    * node returned by {@link Iterator#next()} is this node.
    * <P>
    *
    * Modifying the tree by inserting, removing, or moving a node invalidates any iterators created before the
    * modification.
    *
    * @see #depthFirstIterator(DependencyNode)
    * @see #preorderIterator(DependencyNode)
    * @return an enumeration for traversing the tree in breadth-first order
    */
   public static Iterator<DependencyNode> breadthFirstIterator(DependencyNode dependencyNode)
   {
      return new BreadthFirstIterator(dependencyNode);
   }

   /**
    * Creates and returns an iterator that traverses the subtree rooted at this node in preorder. The first node
    * returned by {@link Iterator#next()} is this node.
    * <P>
    *
    * Modifying the tree by inserting, removing, or moving a node invalidates any iterators created before the
    * modification.
    *
    * @see #depthFirstIterator(DependencyNode)
    * @see #breadthFirstIterator(DependencyNode)
    *
    * @return an enumeration for traversing the tree in breadth-first order
    */
   public static Iterator<DependencyNode> preorderIterator(DependencyNode dependencyNode)
   {
      return new PreorderFirstIterator(dependencyNode);
   }

   public static boolean isForgeAddon(DependencyNode node)
   {
      return isForgeAddon(node.getDependency());
   }

   public static boolean isForgeAddon(Dependency dependency)
   {
      return FORGE_ADDON_CLASSIFIER.equals(dependency.getCoordinate().getClassifier());
   }

   /**
    * Based on {@link DefaultMutableTreeNode#preorderEnumeration()}
    *
    * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
    *
    */
   private static final class PreorderFirstIterator implements Iterator<DependencyNode>
   {

      private Stack<Iterator<DependencyNode>> stack = new Stack<Iterator<DependencyNode>>();

      public PreorderFirstIterator(DependencyNode node)
      {
         stack.push(Collections.singleton(node).iterator());
      }

      @Override
      public boolean hasNext()
      {
         return (!stack.empty() && stack.peek().hasNext());
      }

      @Override
      public DependencyNode next()
      {

         Iterator<DependencyNode> it = stack.peek();
         DependencyNode node = it.next();
         Iterator<DependencyNode> itChildren = node.getChildren().iterator();

         if (!it.hasNext())
         {
            stack.pop();
         }
         if (itChildren.hasNext())
         {
            stack.push(itChildren);
         }
         return node;

      }

      @Override
      public void remove()
      {
         throw new UnsupportedOperationException("remove");
      }
   }

   /**
    * Based on {@link DefaultMutableTreeNode#breadthFirstEnumeration()}
    *
    * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
    *
    */
   private static final class BreadthFirstIterator implements Iterator<DependencyNode>
   {
      private Queue<Iterator<DependencyNode>> queue = new LinkedList<Iterator<DependencyNode>>();

      public BreadthFirstIterator(DependencyNode rootNode)
      {
         queue.add(Collections.singleton(rootNode).iterator());
      }

      @Override
      public boolean hasNext()
      {
         return (!queue.isEmpty() && queue.element().hasNext());
      }

      @Override
      public DependencyNode next()
      {
         Iterator<DependencyNode> it = queue.element();
         DependencyNode node = it.next();
         Iterator<DependencyNode> itChildren = node.getChildren().iterator();
         if (!it.hasNext())
         {
            queue.poll();
         }
         if (itChildren.hasNext())
         {
            queue.add(itChildren);
         }
         return node;
      }

      @Override
      public void remove()
      {
         throw new UnsupportedOperationException("remove");
      }
   }

   /**
    * Based on {@link DefaultMutableTreeNode#postorderEnumeration()}
    *
    * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
    *
    */
   private static final class PostorderIterator implements Iterator<DependencyNode>
   {
      private DependencyNode root;
      private Iterator<DependencyNode> children;
      private Iterator<DependencyNode> subtree;

      public PostorderIterator(DependencyNode node)
      {
         this.root = node;
         this.children = node.getChildren().iterator();
         subtree = Collections.<DependencyNode> emptyList().iterator();
      }

      @Override
      public boolean hasNext()
      {
         return root != null;
      }

      @Override
      public DependencyNode next()
      {
         DependencyNode retval;

         if (subtree.hasNext())
         {
            retval = subtree.next();
         }
         else if (children.hasNext())
         {
            subtree = new PostorderIterator(children.next());
            retval = subtree.next();
         }
         else
         {
            retval = root;
            root = null;
         }

         return retval;
      }

      @Override
      public void remove()
      {
         throw new UnsupportedOperationException("remove");
      }
   }
}
