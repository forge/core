/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource.transaction;

import java.util.Set;

import org.jboss.forge.resource.Resource;

/**
 * A {@link ChangeSet} object contains the modified resources of a specific transaction
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface ChangeSet
{
   /**
    * @return a set of the modified resources
    */
   Set<Resource<?>> getModifiedResources();
}
