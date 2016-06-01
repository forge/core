/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.visit;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResourceVisit
{
   private final Resource<?> root;
   private final VisitContextImpl context = new VisitContextImpl();

   private final ResourceFilter acceptAll = new ResourceFilter()
   {
      @Override
      public boolean accept(Resource<?> resource)
      {
         return true;
      }
   };

   /**
    * Create a new {@link ResourceVisit} instance, beginning at the given root {@link Resource}.
    */
   public ResourceVisit(final Resource<?> root)
   {
      this.root = root;
   }

   /**
    * Perform the visit using the given {@link ResourceVisitor}.
    */
   public void perform(final ResourceVisitor visitor)
   {
      perform(root, visitor, acceptAll, acceptAll);
   }

   /**
    * Perform the visit using the given {@link ResourceVisitor}. All {@link Resource} instances will be recursed, but
    * only resources matching the given {@link ResourceFilter} will be visited.
    */
   public void perform(final ResourceVisitor visitor, final ResourceFilter filter)
   {
      perform(root, visitor, acceptAll, filter);
   }

   /**
    * Perform the visit using the given {@link ResourceVisitor}. All {@link Resource} instances will be recursed, but
    * only resources matching the given {@link ResourceFilter} will be visited.
    * 
    * @param visitor the visitor instance
    * @param recurseFilter the filter that will accept resources to recurse into
    * @param visitFilter the vilter that will accept resources to visit
    */
   public void perform(final ResourceVisitor visitor, final ResourceFilter recurseFilter,
            final ResourceFilter visitFilter)
   {
      perform(root, visitor, recurseFilter, visitFilter);
   }

   private void perform(final Resource<?> root, final ResourceVisitor visitor, final ResourceFilter recurseFilter,
            final ResourceFilter visitFilter)
   {
      perform(root, context, visitor, recurseFilter, visitFilter);
   }

   private void perform(Resource<?> root, VisitContextImpl context, ResourceVisitor visitor,
            ResourceFilter recurseFilter, ResourceFilter visitFilter)
   {
      if (!context.isTerminated())
      {
         if (visitFilter.accept(root))
            visitor.visit(context, root);

         if (recurseFilter.accept(root))
         {
            for (Resource<?> child : root.listResources())
            {
               perform(child, visitor, recurseFilter, visitFilter);
               if (context.isTerminated())
                  break;
            }
         }
      }
   }

   private static class VisitContextImpl implements VisitContext
   {
      private boolean terminated;

      @Override
      public void terminate()
      {
         this.terminated = true;
      }

      boolean isTerminated()
      {
         return terminated;
      }
   }

   public boolean isTerminated()
   {
      return context.isTerminated();
   }
}
