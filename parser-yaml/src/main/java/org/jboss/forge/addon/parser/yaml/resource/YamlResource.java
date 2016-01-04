/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.yaml.resource;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;

/**
 * A {@link Resource} that represents a YAML (Yet Another Markup language) Resource
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface YamlResource extends FileResource<YamlResource>
{
   /**
    * @return an {@link Optional} YAML {@link Map} representation of the underlying {@link File}
    * @throws ResourceException if the {@link File} cannot be read properly
    */
   Optional<Map<String, Object>> getModel();

   /**
    * @return A list of YAML {@link Map} of the underlying {@link File} when it contains several documents.
    * @throws ResourceException if the {@link File} cannot be read properly
    */
   List<Map<String, Object>> getAllModel();

   /**
    * Writes the contents from the {@link Map} object to the underlying {@link File}
    * 
    * @param data the {@link Map} object. May not be <code>null</code>.
    * @return this instance
    */
   YamlResource setContents(Map<String, Object> data);

   /**
    * Writes the contents from the {@link List} object to the underlying {@link File}
    * 
    * @param data the {@link List} object. May not be <code>null</code>.
    * @return this instance
    */
   YamlResource setContents(List<Map<String, Object>> data);
}
