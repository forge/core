/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.facets.generic;

import org.jboss.forge.addon.projects.facets.MetadataFacet;

/**
 * Generic implementation for {@link MetadataFacet}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface GenericMetadataFacet extends MetadataFacet
{
   /**
    * The file that contains the metadata
    */
   final String PROJECT_METADATA_FILE_NAME = ".forge-metadata.properties";
}
