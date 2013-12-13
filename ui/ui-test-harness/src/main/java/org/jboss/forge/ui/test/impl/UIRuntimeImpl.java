/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.test.impl;

import org.jboss.forge.addon.ui.DefaultUIProgressMonitor;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.spi.UIRuntime;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class UIRuntimeImpl implements UIRuntime
{
   private final UIProgressMonitor progressMonitor = new DefaultUIProgressMonitor();

   public UIRuntimeImpl()
   {
   }

   @Override
   public UIProgressMonitor createProgressMonitor(UIContext context)
   {
      return progressMonitor;
   }

}
