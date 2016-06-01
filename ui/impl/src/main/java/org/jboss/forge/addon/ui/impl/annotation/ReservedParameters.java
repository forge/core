/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.annotation;

import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

/**
 * Used in {@link AnnotationCommandAdapter}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class ReservedParameters
{
   public static boolean isReservedParameter(Class<?> type)
   {
      return type == UIContext.class
               || type == UIPrompt.class
               || type == UIOutput.class
               || type == UIProgressMonitor.class
               || type == UIProvider.class
               || type == UIExecutionContext.class
               || type == UIDesktop.class;
   }

   public static Object getReservedParameter(UIExecutionContext context, Class<?> type)
   {
      if (type == UIContext.class)
      {
         return context.getUIContext();
      }
      else if (type == UIProvider.class)
      {
         return context.getUIContext().getProvider();
      }
      else if (type == UIPrompt.class)
      {
         return context.getPrompt();
      }
      else if (type == UIOutput.class)
      {
         return context.getUIContext().getProvider().getOutput();
      }
      else if (type == UIProgressMonitor.class)
      {
         return context.getProgressMonitor();
      }
      else if (type == UIExecutionContext.class)
      {
         return context;
      }
      else if (type == UIDesktop.class)
      {
         return context.getUIContext().getProvider().getDesktop();
      }
      else
      {
         return null;
      }
   }

}
