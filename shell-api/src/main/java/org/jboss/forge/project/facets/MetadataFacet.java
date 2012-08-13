/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.facets;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.dependencies.Dependency;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface MetadataFacet extends Facet
{
   void setProjectName(String name);

   String getProjectName();

   void setTopLevelPackage(String groupId);

   String getTopLevelPackage();

   String getProjectVersion();

   Dependency getOutputDependency();

}
