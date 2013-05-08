/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.scaffoldx.plugins.shell;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.scaffoldx.ScaffoldProvider;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ScaffoldProviderCompleter extends SimpleTokenCompleter
{
   @Inject
   @Any
   private Instance<ScaffoldProvider> impls;

   @Override
   public List<Object> getCompletionTokens()
   {
      List<Object> result = new ArrayList<Object>();
      for (ScaffoldProvider impl : impls)
      {
         result.add(ConstraintInspector.getName(impl.getClass()));
      }
      return result;
   }

}
