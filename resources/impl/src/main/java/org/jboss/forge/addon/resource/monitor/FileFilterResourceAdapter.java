/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.monitor;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;

/**
 * A {@link FileFilter} adapter for {@link ResourceFilter}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FileFilterResourceAdapter extends AbstractFileFilter
{
   private final ResourceFactory resourceFactory;
   private final ResourceFilter filter;

   public FileFilterResourceAdapter(ResourceFactory resourceFactory, ResourceFilter filter)
   {
      super();
      this.resourceFactory = resourceFactory;
      this.filter = filter;
   }

   @Override
   public boolean accept(File pathname)
   {
      Resource<?> resource = resourceFactory.create(pathname);
      boolean accept = filter.accept(resource);
      return accept;
   }
}
