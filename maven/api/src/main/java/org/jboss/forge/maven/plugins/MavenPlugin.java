/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.plugins;

import java.util.List;

import org.jboss.forge.dependencies.Dependency;

/**
 * Represents a Maven plugin
 *
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

public interface MavenPlugin extends PluginElement
{
   Dependency getDependency();

   Configuration getConfig();

   List<Execution> listExecutions();

   boolean isExtensionsEnabled();

   List<Dependency> getDirectDependencies();

}
