/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Represents any regular file which Furnace does not have any special handler for.
 * 
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FileResourceImpl extends AbstractFileResource<FileResourceImpl>
{
   public FileResourceImpl(final ResourceFactory factory, final File file)
   {
      super(factory, file);
   }

   @Override
   public FileResourceImpl createFrom(final File file)
   {
      return new FileResourceImpl(getResourceFactory(), file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public boolean supports(ResourceFacet type)
   {
      return false;
   }
}
