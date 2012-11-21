/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.git.gitignore;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.VirtualResource;

/**
 * @author Dan Allen
 */
public class GitIgnorePatternResource extends VirtualResource<String>
{

   private String pattern;

   protected GitIgnorePatternResource(Resource<?> parent, String pattern)
   {
      super(parent);
      this.pattern = pattern;
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   @Override
   public boolean delete(boolean recursive)
            throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Recursive deletion does not apply");
   }

   @Override
   public String getName()
   {
      return pattern;
   }

   @Override
   public String getUnderlyingResourceObject()
   {
      return pattern;
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public String toString()
   {
      return getName();
   }
}
