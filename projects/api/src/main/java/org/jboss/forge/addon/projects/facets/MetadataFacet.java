/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.facets;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface MetadataFacet extends ProjectFacet
{
   String getProjectName();

   void setProjectName(String name);

   String getTopLevelPackage();

   void setTopLevelPackage(String groupId);

   String getProjectVersion();

   void setProjectVersion(String version);

   Dependency getOutputDependency();

}
