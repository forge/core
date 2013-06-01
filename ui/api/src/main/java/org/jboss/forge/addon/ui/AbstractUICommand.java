/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * This class provides a skeletal implementation of the <tt>UICommand</tt> interface
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractUICommand implements UICommand
{
   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public Metadata getMetadata()
   {
      return Metadata.forCommand(getClass());
   }

   @Override
   public void validate(UIValidationContext validator)
   {
   }
}
