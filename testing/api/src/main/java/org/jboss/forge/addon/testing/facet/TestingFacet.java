/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.testing.facet;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.projects.ProjectFacet;

import java.util.List;

/**
 * Provides basic functionality for managing project tests.
 *
 * A project can use one of the supported testing frameworks.
 *
 * @author Ivan St. Ivanov
 */
public interface TestingFacet extends ProjectFacet
{
   /**
    * Returns the testing framework name.
    */
   String getFrameworkName();

   /**
    * Returns a list of all the selected testing framework dependencies.
    */
   List<Dependency> getFrameworkDependencies();

   /**
    * Returns a list of the versions of the selected testing frameworks, that are available in the central repository.
    */
   List<String> getAvailableVersions();

   /**
    * Sets the version of the testing framework.
    *
    * Make sure to call this method prior to installing this facet.
    */
   void setFrameworkVersion(String version);
}
