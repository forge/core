/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.facets;

import org.jboss.forge.project.Facet;

public interface JavaExecutionFacet extends Facet
{
   void executeProjectClass(String fullyQualifiedClassName, String... aruments);
}
