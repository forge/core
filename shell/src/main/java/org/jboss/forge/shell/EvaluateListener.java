/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.shell.events.PostStartup;

/**
 * Handles the -e --evaluate command line option and shuts down the shell.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class EvaluateListener
{
   @Inject
   private Shell shell;

   public void evaluate(@Observes PostStartup event)
   {
      String evaluate = System.getProperty(Bootstrap.PROP_EVALUATE);
      if (evaluate != null)
      {
         try
         {
            shell.execute(evaluate);
            System.exit(0);
         }
         catch (Exception e)
         {
            System.exit(1);
         }
      }
   }
}
