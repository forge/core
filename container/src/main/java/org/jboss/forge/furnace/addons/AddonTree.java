/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.addons;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonDependency;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.impl.AddonImpl;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.util.ValuedVisitor;
import org.jboss.forge.furnace.util.Visitor;
import org.jboss.forge.furnace.versions.SingleVersionRange;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonTree implements Iterable<Addon>
{
   private AddonImpl root;
   private LockManager lock;

   public AddonTree(LockManager lock)
   {
      this.lock = lock;
      this.root = new AddonImpl(lock, AddonId.from("ROOT ADDON NODE", UUID.randomUUID().toString()));
   }

   public void add(final Addon addon)
   {
      for (Addon existing : this)
      {
         if (existing.equals(addon) || existing.getId().equals(addon.getId()))
            throw new IllegalArgumentException("Cannot add duplicate Addon [" + addon + "] to the registry.");
      }
      reattach(addon);
   }

   public void reattach(Addon addon)
   {
      if (!contains(addon))
      {
         root.getMutableDependencies().add(
                  new AddonDependencyImpl(lock, root, new SingleVersionRange(addon.getId().getVersion()), addon,
                           false, false));
         prune();
      }
   }

   /**
    * Check to see if any root {@link AddonDependency} instances are also a dependency of another {@link Addon}. Remove
    * any such duplicate edges from the root {@link Addon} to preserve the tree traversal order.
    */
   public void prune()
   {
      final Set<AddonDependency> duplicateEdge = new HashSet<AddonDependency>();

      breadthFirst(new Visitor<Addon>()
      {
         @Override
         public void visit(Addon instance)
         {
            if (!root.equals(instance))
            {
               for (AddonDependency dep : instance.getDependencies())
               {
                  for (AddonDependency rootDependency : root.getDependencies())
                  {
                     if (rootDependency.getDependency().equals(dep.getDependency()))
                     {
                        duplicateEdge.add(rootDependency);
                     }
                  }
               }
            }
         }
      });

      for (AddonDependency rootDependency : duplicateEdge)
      {
         root.getMutableDependencies().remove(rootDependency);
      }
   }

   /*
    * Traversal methods.
    */
   public synchronized void depthFirst(Visitor<Addon> visitor)
   {
      Set<Addon> seen = new HashSet<Addon>();
      seen.add(root);
      visitDepthFirst(seen, root, visitor);
   }

   private void visitDepthFirst(Set<Addon> seen, Addon addon, Visitor<Addon> visitor)
   {
      for (AddonDependency dep : addon.getDependencies())
      {
         visitDepthFirst(seen, dep.getDependency(), visitor);
      }

      if (!seen.contains(addon))
      {
         visitor.visit(addon);
         seen.add(addon);
      }
   }

   public synchronized void breadthFirst(Visitor<Addon> visitor)
   {
      Queue<Addon> queue = new LinkedList<Addon>();
      Set<Addon> seen = new HashSet<Addon>();

      seen.add(root);
      queue.add(root);

      while (!queue.isEmpty())
      {
         Addon addon = queue.remove();
         for (AddonDependency dep : addon.getDependencies())
         {
            Addon dependency = dep.getDependency();
            if (!seen.contains(dependency))
            {
               visitor.visit(dependency);
               queue.add(dependency);
               seen.add(dependency);
            }
         }
      }
   }

   @Override
   public Iterator<Addon> iterator()
   {
      ValuedVisitor<Set<Addon>, Addon> visitor = new ValuedVisitor<Set<Addon>, Addon>()
      {
         {
            setResult(new HashSet<Addon>());
         }

         @Override
         public void visit(Addon instance)
         {
            if (!root.equals(instance))
               getResult().add(instance);
         }
      };

      breadthFirst(visitor);

      return visitor.getResult().iterator();
   }

   /**
    * Return true if this {@link AddonTree} contians the given {@link Addon}.
    */
   public boolean contains(final Addon addon)
   {
      ValuedVisitor<AddonImpl, Addon> visitor = new ValuedVisitor<AddonImpl, Addon>()
      {
         @Override
         public void visit(Addon instance)
         {
            if (instance.equals(addon))
            {
               setResult((AddonImpl) instance);
            }
         }
      };

      depthFirst(visitor);

      return visitor.hasResult();
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      breadthFirst(new Visitor<Addon>()
      {
         @Override
         public void visit(Addon instance)
         {
            result.append(instance.toString());

            if (((AddonImpl) instance).isDirty())
               result.append(" - dirty");

            result.append("\n");
         }
      });
      return result.toString();
   }
}
