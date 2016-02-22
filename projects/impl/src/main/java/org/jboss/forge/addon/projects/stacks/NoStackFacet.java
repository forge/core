/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.stacks;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;

/**
 * This is a {@link StackFacet} implementation placeholder
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class NoStackFacet extends AbstractFacet<Project> implements StackFacet
{
   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return false;
   }

   @Override
   public Stack getStack()
   {
      return NoStack.INSTANCE;
   }

   @Override
   public String toString()
   {
      return "None";
   }
}
