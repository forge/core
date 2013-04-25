/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

import java.util.Set;

import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.util.Visitor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MarkDisabledLoadedAddonsDirtyVisitor implements Visitor<Addon>
{
   private AddonTree tree;
   private Set<AddonId> enabled;

   public MarkDisabledLoadedAddonsDirtyVisitor(AddonTree tree, Set<AddonId> enabled)
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
         if (!enabled.contains(addon.getId()) && addon.getStatus().isLoaded())
         {
            addon.setDirty(true);
            tree.depthFirst(new MarkLoadedAddonsDirtyVisitor(tree, addon));
         }
      }
   }
}
