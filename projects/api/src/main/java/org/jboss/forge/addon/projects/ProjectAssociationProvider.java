/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.resource.Resource;

/**
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectAssociationProvider
{
   /**
    * Return true if this provide is capable of creating a parent-child association between the given {@link Project}
    * and the given parent {@link Resource}.
    */
   boolean canAssociate(Project project, Resource<?> parent);

   /**
    * Create a parent-child association between the given {@link Project} and the given parent {@link Resource}.
    */
   void associate(Project project, Resource<?> parent);
}
