/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EnvironmentPropertiesCompleter extends SimpleTokenCompleter
{
   @Inject
   private ForgeEnvironment environment;

   @Override
   public List<Object> getCompletionTokens()
   {
      Map<String, Object> props = environment.getProperties();
      return new ArrayList<Object>(props.keySet());
   }
}
