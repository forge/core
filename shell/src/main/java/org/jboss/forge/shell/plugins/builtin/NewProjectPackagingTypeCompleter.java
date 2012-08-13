/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.Arrays;

import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

public class NewProjectPackagingTypeCompleter extends SimpleTokenCompleter
{
   @Override
   public Iterable<?> getCompletionTokens()
   {
      return Arrays.asList(PackagingType.BASIC, PackagingType.JAR, PackagingType.WAR, PackagingType.BUNDLE);
   }
}