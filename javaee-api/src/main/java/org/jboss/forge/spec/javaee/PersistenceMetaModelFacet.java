/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.dependencies.Dependency;

public interface PersistenceMetaModelFacet extends Facet
{
   String getProcessor();
   
   String getCompilerArgs();
   
   Dependency getProcessorDependency();
}
