/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.building;

/**
 * Returns the build result of a project
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface BuildResult
{
   /**
    * Returns if the project build succeeded
    */
   boolean isSuccess();

   /**
    * Returns the build messages (if any), never null
    */
   Iterable<BuildMessage> getMessages();
}
