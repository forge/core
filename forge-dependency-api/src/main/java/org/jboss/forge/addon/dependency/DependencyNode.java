/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

import java.util.List;

public interface DependencyNode
{
   public Dependency getDependency();

   public List<DependencyNode> getChildren();

   public DependencyNode getParent();
}
