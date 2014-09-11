/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.facets;

import java.util.logging.LogRecord;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * A Facet to check the {@link Project} build status
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface BuildStatusFacet extends ProjectFacet
{
   /**
    * Returns if the project model is buildable
    */
   boolean isBuildable();

   /**
    * Returns the build messages (if any), never null
    */
   Iterable<LogRecord> getBuildMessages();
}
