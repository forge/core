/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.project;

import java.util.Set;

import org.jboss.forge.project.Facet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface FacetRegistry
{
   /**
    * Return all known {@link Facet} types.
    */
   Set<Class<? extends Facet>> getFacetTypes();
}
