/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

import java.util.List;

import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.util.Callables;
import org.jboss.forge.container.util.Visitor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class StopDisabledAddonsVisitor implements Visitor<Addon>
{
   private AddonTree tree;
   private List<AddonImpl> enabled;

   public StopDisabledAddonsVisitor(AddonTree tree, List<AddonImpl> enabled)
   {
      this.tree = tree;
      this.enabled = enabled;
   }

   @Override
   public void visit(Addon instance)
   {
      if (instance instanceof AddonImpl)
      {
         AddonImpl addon = (AddonImpl) instance;
         if (!enabled.contains(addon))
         {
            tree.depthFirst(new MarkAddonDirtyVisitor(tree, addon));
            Callables.call(new StopAddonCallable(tree, addon));
         }
      }
   }

}
