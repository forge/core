/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

import java.util.List;

import org.jboss.forge.addon.dependency.collection.DependencyFilter;

/**
 * A parameter object which is used to search dependencies
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface DependencyQuery
{
   Coordinate getCoordinate();

   String getScopeType();

   DependencyFilter getDependencyFilter();

   List<DependencyRepository> getDependencyRepositories();

}