/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.Arrays;
import java.util.List;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.forge.addon.shell.ShellImpl;

/**
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCompositeCompletion implements Completion
{
   private ShellImpl shell;
   private List<Completion> completions;

   public ForgeCompositeCompletion(ShellImpl shell, Completion... completions)
   {
      this.shell = shell;
      this.completions = Arrays.asList(completions);
   }

   @Override
   public void complete(CompleteOperation completeOperation)
   {
      for (Completion completion : completions)
      {
         completion.complete(completeOperation);
         shell.incrementCompletionCount();
      }
   }

}
