/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import java.util.List;

/**
 * If a {@link Resource} is deleted, and the {@link Resource} object implements this interface, the methods of this
 * interface will be called.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface DeletionAware
{
   /**
    * Returns a {@link List} of {@link Resource} which do not need confirmation to delete
    *
    */
   List<Resource<?>> getResources();

   /**
    * Returns a {@link List} of {@link Resource} as candidates for deletion
    *
    */
   List<Resource<?>> getOptionalResources();
}
