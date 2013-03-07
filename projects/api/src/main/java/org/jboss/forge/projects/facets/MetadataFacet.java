/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.projects.facets;

import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.projects.ProjectFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface MetadataFacet extends ProjectFacet
{
   void setProjectName(String name);

   String getProjectName();

   void setTopLevelPackage(String groupId);

   String getTopLevelPackage();

   String getProjectVersion();

   Dependency getOutputDependency();

}
