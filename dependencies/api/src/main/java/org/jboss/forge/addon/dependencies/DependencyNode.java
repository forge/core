/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies;

import java.util.List;

public interface DependencyNode
{
   public DependencyNode getParent();
   
   public Dependency getDependency();

   public List<DependencyNode> getChildren();
}
