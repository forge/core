/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.util.Visitor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CheckDirtyStatusVisitor implements Visitor<Addon>
{
   boolean dirty = false;

   @Override
   public void visit(Addon instance)
   {
      if (((AddonImpl) instance).isDirty())
         dirty = true;
   }

   public boolean isDirty()
   {
      return dirty;
   }
}
