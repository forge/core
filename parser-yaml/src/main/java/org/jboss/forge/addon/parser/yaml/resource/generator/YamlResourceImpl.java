/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.yaml.resource.generator;

import java.io.File;

import org.jboss.forge.addon.parser.yaml.resource.AbstractYamlResource;
import org.jboss.forge.addon.parser.yaml.resource.YamlResource;
import org.jboss.forge.addon.resource.ResourceFactory;

/**
 * Default implementation of {@link YamlResource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
class YamlResourceImpl extends AbstractYamlResource
{
   public YamlResourceImpl(ResourceFactory factory, File file)
   {
      super(factory, file);
   }

   @Override
   public YamlResourceImpl createFrom(File file)
   {
      return new YamlResourceImpl(getResourceFactory(), file);
   }
}
