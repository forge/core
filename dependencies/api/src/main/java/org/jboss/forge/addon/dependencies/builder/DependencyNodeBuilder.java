/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.builder;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyNode;
import org.jboss.forge.addon.dependencies.collection.DependencyNodeUtil;

/**
 * Creates a {@link DependencyNode} object
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class DependencyNodeBuilder implements DependencyNode
{
   private DependencyNode parent;
   private Dependency dependency;
   private List<DependencyNode> children = new ArrayList<DependencyNode>();

   private DependencyNodeBuilder(DependencyNode parent, Dependency dependency)
   {
      this.parent = parent;
      this.dependency = dependency;
   }

   public static DependencyNodeBuilder create(DependencyNode parent, Dependency dependency)
   {
      DependencyNodeBuilder builder = new DependencyNodeBuilder(parent, dependency);
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
   public DependencyNode getParent()
   {
      return parent;
   }

   @Override
   public String toString()
   {
      return DependencyNodeUtil.prettyPrint(this).toString();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dependency == null) ? 0 : dependency.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DependencyNodeBuilder other = (DependencyNodeBuilder) obj;
      if (dependency == null)
      {
         if (other.dependency != null)
            return false;
      }
      else if (!dependency.equals(other.dependency))
         return false;
      return true;
   }
}
