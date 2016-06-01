/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.profiles;

import java.util.List;
import java.util.Properties;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyRepository;

public interface Profile
{
   String getId();

   boolean isActiveByDefault();

   List<Dependency> listDependencies();

   List<DependencyRepository> listRepositories();

   Properties getProperties();

   org.apache.maven.model.Profile getAsMavenProfile();
}
