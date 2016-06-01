/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.methods;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.PropertySource;

public interface GetSetMethodGenerator
{

   MethodSource<JavaClassSource> createAccessor(PropertySource<JavaClassSource> property);
   MethodSource<JavaClassSource> createMutator(PropertySource<JavaClassSource> property);
  
   boolean isCorrectAccessor(MethodSource<JavaClassSource> method, PropertySource<JavaClassSource> property);
   boolean isCorrectMutator(MethodSource<JavaClassSource> method, PropertySource<JavaClassSource> property);
}
