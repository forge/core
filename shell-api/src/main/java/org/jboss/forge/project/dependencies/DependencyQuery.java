/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import java.util.List;

/**
 * A parameter object which is used to search dependencies
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface DependencyQuery
{

   public abstract Dependency getDependency();

   public abstract List<DependencyRepository> getDependencyRepositories();

   public abstract DependencyFilter getDependencyFilter();

}