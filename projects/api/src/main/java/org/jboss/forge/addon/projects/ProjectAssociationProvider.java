/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.container.services.Exported;

/**
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ProjectAssociationProvider
{
   /**
    * Return true if this provide is capable of creating a parent-child association between the given Project and the
    * given parent directory.
    */
   boolean canAssociate(Project project, DirectoryResource parent);

   /**
    * Create a parent-child association between the given Project and the given parent directory.
    */
   void associate(Project project, DirectoryResource parent);
}
