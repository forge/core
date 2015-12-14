/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl;

import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.generic.AbstractGenericProjectProvider;
import org.jboss.forge.addon.projects.generic.GenericProjectProvider;

/**
 * Generic implementation of {@link ProjectProvider}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenericProjectProviderImpl extends AbstractGenericProjectProvider implements GenericProjectProvider
{
   @Override
   public String getType()
   {
      return "None";
   }

   @Override
   public int priority()
   {
      return Integer.MAX_VALUE;
   }
}